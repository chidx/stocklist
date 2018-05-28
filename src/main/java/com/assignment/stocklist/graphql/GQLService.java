package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
@Service
@RequiredArgsConstructor
public class GQLService {

    private static final String STOCK_ADDED = "STOCK_ADDED";
    private static final String PRICE_UPDATED = "PRICE_UPDATED";
    private static final String ALL_UPDATES = "ALL_UPDATES";
    private static final GQLStock NULL_PREVIOUS_VALUE = null;
    private final StockRepository stockRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final Map<String, FluxSink<GQLSubscriptionPayload>> sinks = new ConcurrentHashMap<>();

    @GraphQLQuery(name = "stocks")
    public List<GQLStock> getStocks() {
        List<Stock> gqlStocks = (List<Stock>) stockRepository.findAll();
        return gqlStocks.stream()
            .map(this::composeGqlStock)
            .collect(Collectors.toList());
    }

    @GraphQLQuery(name = "stock")
    public GQLStock getStock(@GraphQLArgument(name = "name") String name) {
        Stock stock = stockRepository.findByName(name);
        if (stock == null) {
            throw new GraphQLException("Stock with name " + name + " not found");
        }
        return composeGqlStock(stock);
    }

    @GraphQLMutation
    public GQLStock addStock(@GraphQLArgument(name = "name") String name,
        @GraphQLArgument(name = "price") Float price) {
        Stock maybeStock = stockRepository.findByName(name);
        if (maybeStock != null) {
            throw new GraphQLException("There is already stock with name. Please use update price");
        }
        Stock stock = stockRepository.save(new Stock(name, price, System.currentTimeMillis()));
        addNewItemToSinks(NULL_PREVIOUS_VALUE, composeGqlStock(stock), STOCK_ADDED);
        addNewItemToSinks(NULL_PREVIOUS_VALUE, composeGqlStock(stock), ALL_UPDATES);
        return composeGqlStock(stock);
    }

    @GraphQLMutation
    public GQLStock updatePrice(@GraphQLArgument(name = "id") Long id,
        @GraphQLArgument(name = "price") Float price) {
        Stock maybeStock = stockRepository.findById(id)
            .orElseThrow(() -> new GraphQLException("stock with id " + id + " not found"));
        GQLStock previousValue = composeGqlStock(maybeStock);
        priceHistoryRepository.save(new PriceHistory(maybeStock.getId(),
            maybeStock.getCurrentPrice(), maybeStock.getLastUpdate()));
        maybeStock.setCurrentPrice(price);
        maybeStock.setLastUpdate(System.currentTimeMillis());
        Stock updated = stockRepository.save(maybeStock);
        GQLStock newValue = composeGqlStock(updated);
        addNewItemToSinks(previousValue, newValue, PRICE_UPDATED);
        addNewItemToSinks(previousValue, newValue, ALL_UPDATES);
        return composeGqlStock(updated);
    }

    private void addNewItemToSinks(GQLStock previousValue, GQLStock newValue,
        String topic) {
        if (sinks.containsKey(topic)) {
            log.info("Adding new item to stream " + topic);
            sinks.get(topic).next(new GQLSubscriptionPayload(previousValue, newValue));
        }
    }

    @GraphQLSubscription(name = "stockSubscription", description = "Stock subscription")
    public Publisher<GQLSubscriptionPayload> stockSubscription(
        @GraphQLArgument(name = "status") StatusEnum statusEnum) {
        log.info("Incoming GraphQL subscription coming...");
        if (StatusEnum.CREATED == statusEnum) {
            return Flux.create(fluxSink -> sinks.put(STOCK_ADDED, fluxSink));
        } else if (StatusEnum.UPDATED == statusEnum) {
            return Flux.create(fluxSink -> sinks.put(PRICE_UPDATED, fluxSink));
        } else {
            return Flux.create(fluxSink -> sinks.put(ALL_UPDATES, fluxSink));
        }
    }

    @GraphQLSubscription(name = "timer", description = "Timer subscription")
    public Publisher<Timer> timer() {
        Observable<Timer> observable = Observable
            .interval(1, TimeUnit.SECONDS)
            .flatMap(n -> Observable.create(observableEmitter -> {
                observableEmitter.onNext(new Timer(LocalDateTime.now()));
            }));

        return observable.toFlowable(BackpressureStrategy.BUFFER);
    }

    private GQLStock composeGqlStock(Stock stock) {
        GQLStock gqlStock = new GQLStock(stock.getId(), stock.getName(), stock.getCurrentPrice(),
            stock.getLastUpdate(), getPriceHistoryByStockId(stock.getId()));
        return gqlStock;
    }

    private List<GQLPriceHistory> getPriceHistoryByStockId(Long stockId) {
        return priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(stockId)
            .stream()
            .map(priceHistory -> new GQLPriceHistory(priceHistory.getStockId(),
                priceHistory.getPrice(), priceHistory.getTimestamp()))
            .collect(Collectors.toList());
    }
}
