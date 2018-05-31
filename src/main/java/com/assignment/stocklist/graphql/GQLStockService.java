package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GQLStockService {

    private static final String STOCK_ADDED = "STOCK_ADDED";
    private static final String PRICE_UPDATED = "PRICE_UPDATED";
    private static final String ALL_UPDATES = "ALL_UPDATES";
    private static final GQLStock NULL_PREVIOUS_VALUE = null;
    private final StockRepository stockRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final FluxSink fluxSink;

    public List<GQLStock> getAllStocks() {
        return ((List<Stock>) stockRepository.findAll())
            .stream()
            .map(this::createGQLStock)
            .collect(Collectors.toList());
    }

    public GQLStock getStock(String name) {
        Stock maybeStock = stockRepository.findByName(name)
            .orElseThrow(() -> new GraphQLException("stock with name " + name + " not found"));
        return createGQLStock(maybeStock);
    }

    public GQLStock addStock(String name, Float price) {
        Stock stock = new Stock(name, price, System.currentTimeMillis());
        Stock savedStock = stockRepository.save(stock);
        addNewItemToSinks(NULL_PREVIOUS_VALUE, createGQLStock(stock), STOCK_ADDED);
        addNewItemToSinks(NULL_PREVIOUS_VALUE, createGQLStock(stock), ALL_UPDATES);
        return createGQLStock(savedStock);
    }

    public GQLStock updateStockPrice(String id, Float price) {
        Long idLong = Long.parseLong(id);
        log.info("id: " + id + " (casted): " + idLong);
        Stock maybeStock = stockRepository.findById(idLong)
            .orElseThrow(() -> new GraphQLException("stock with id " + id + " not found"));
        GQLStock previousValue = createGQLStock(maybeStock);
        priceHistoryRepository.save(new PriceHistory(maybeStock.getId(),
            maybeStock.getCurrentPrice(), maybeStock.getLastUpdate()));
        maybeStock.setCurrentPrice(price);
        maybeStock.setLastUpdate(System.currentTimeMillis());
        Stock updated = stockRepository.save(maybeStock);
        GQLStock newValue = createGQLStock(updated);
        log.info("old: " + previousValue.getCurrentPrice());
        log.info("new: " + newValue.getCurrentPrice());
        addNewItemToSinks(previousValue, newValue, PRICE_UPDATED);
        addNewItemToSinks(previousValue, newValue, ALL_UPDATES);
        return createGQLStock(newValue);
    }

    private void addNewItemToSinks(GQLStock previousValue, GQLStock newValue, String topic) {
        if (fluxSink.getSinks().containsKey(topic)) {
            log.info("Adding new item to stream " + topic);
            log.info("old: " + (previousValue == null ? "" : previousValue.getCurrentPrice()));
            log.info("new: " + newValue.getCurrentPrice());
            fluxSink.getSinks().get(topic)
                .next(new GQLSubscriptionPayload(previousValue, newValue));
        }
    }

    public String getName(GQLStock stock) {
        return getStock(stock).getName();
    }

    public Float getCurrentPrice(GQLStock stock) {
        return getStock(stock).getCurrentPrice();
    }

    public Long getLastUpdate(GQLStock stock) {
        return getStock(stock).getLastUpdate();
    }

    public List<GQLPriceHistory> getPriceHistory(GQLStock stock) {
        Stock maybeStock = getStock(stock);
        return getPriceHistoryByStockId(maybeStock.getId());
    }

    private List<GQLPriceHistory> getPriceHistoryByStockId(Long stockId) {
        return priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(stockId)
            .stream()
            .map(priceHistory -> new GQLPriceHistory(priceHistory.getStockId(),
                priceHistory.getPrice(), priceHistory.getTimestamp()))
            .collect(Collectors.toList());
    }

    @NotNull
    private GQLStock createGQLStock(Stock stock) {
        GQLStock gqlStock = new GQLStock(stock.getId(), stock.getName(), stock.getCurrentPrice(), stock.getLastUpdate());
        gqlStock.setPriceHistory(getPriceHistoryByStockId(gqlStock.getId()));
        return gqlStock;
    }

    @NotNull
    private Stock getStock(GQLStock stock) {
        return stockRepository.findByName(stock.getName())
            .orElseThrow(() -> new GraphQLException("stock with name " + stock.getName() + " not found"));
    }
}
