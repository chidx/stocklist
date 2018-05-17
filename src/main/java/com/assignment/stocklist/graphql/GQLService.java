package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
@Service
@GraphQLApi
@RequiredArgsConstructor
public class GQLService {

    private static final String STOCK_ADDED = "stockAdded";
    private static final String PRICE_UPDATED = "PRICE_UPDATED";
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
        return composeGqlStock(stock);
    }

    @GraphQLMutation
    public GQLStock updatePrice(@GraphQLArgument(name = "id") String id,
        @GraphQLArgument(name = "price") Float price) {
        Long idLong = Long.parseLong(id);
        log.info("id: " + id + " (casted): " + idLong);
        Stock maybeStock = stockRepository.findById(idLong)
            .orElseThrow(() -> new GraphQLException("stock with id " + id + " not found"));
        GQLStock previousValue = composeGqlStock(maybeStock);
        priceHistoryRepository.save(new PriceHistory(maybeStock.getId(),
            maybeStock.getCurrentPrice(), maybeStock.getLastUpdate()));
        maybeStock.setCurrentPrice(price);
        maybeStock.setLastUpdate(System.currentTimeMillis());
        Stock updated = stockRepository.save(maybeStock);
        GQLStock newValue = composeGqlStock(updated);
        addNewItemToSinks(previousValue, newValue, PRICE_UPDATED);
        return new GQLStock(updated.getId(), updated.getName(), updated.getCurrentPrice(),
            updated.getLastUpdate(), getPriceHistoryByStockId(idLong));
    }

    private void addNewItemToSinks(GQLStock previousValue, GQLStock newValue,
        String topic) {
        if (sinks.containsKey(topic)) {
            sinks.get(topic).next(new GQLSubscriptionPayload(previousValue, newValue));
        }
    }

    @GraphQLSubscription
    public Publisher<GQLSubscriptionPayload> stockAdded() {
        return Flux.create(fluxSink -> sinks.put(STOCK_ADDED, fluxSink));
    }

    @GraphQLSubscription
    public Publisher<GQLSubscriptionPayload> priceUpdated() {
        return Flux.create(fluxSink -> sinks.put(PRICE_UPDATED, fluxSink));
    }

    private GQLStock composeGqlStock(Stock stock) {
        return new GQLStock(stock.getId(), stock.getName(), stock.getCurrentPrice(),
            stock.getLastUpdate(), getPriceHistoryByStockId(stock.getId()));
    }

    private List<GQLPriceHistory> getPriceHistoryByStockId(Long stockId) {
        return priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(stockId)
            .stream()
            .map(priceHistory -> new GQLPriceHistory(priceHistory.getStockId(),
                priceHistory.getPrice(), priceHistory.getTimestamp()))
            .collect(Collectors.toList());
    }
}
