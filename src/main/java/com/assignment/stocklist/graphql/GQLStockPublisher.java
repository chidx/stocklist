package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observables.ConnectableObservable;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class GQLStockPublisher {

    private static final int FIRST_INDEX = 0;
    private final Flowable<GQLSubscriptionPayload> publisher;
    private final StockRepository stockRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public GQLStockPublisher(StockRepository stockRepository, PriceHistoryRepository priceHistoryRepository) {
        Observable<GQLSubscriptionPayload> stockObservable = Observable.create(emitter -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(newStockUpdate(emitter), 0, 500, TimeUnit.MILLISECONDS);
        });

        ConnectableObservable<GQLSubscriptionPayload> connectableObservable = stockObservable.share().publish();
        connectableObservable.connect();

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER);
        this.stockRepository = stockRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    private Runnable newStockUpdate(ObservableEmitter<GQLSubscriptionPayload> emitter) {
        return () -> {
            List<Stock> stockList = (List<Stock>) stockRepository.findAll();
            if (stockList != null && !stockList.isEmpty()) {
                Stock lastStock = stockList.get(stockList.size() - 1);
                Long diff = System.currentTimeMillis() - lastStock.getLastUpdate();
                if (diff <= 500) {
                    log.info("Emit subscription");
                    GQLSubscriptionPayload subscriptionPayload =
                        getSubscriptionPayload(lastStock);
                    emitSubscriptionPayload(emitter, subscriptionPayload);
                }
            }
        };
    }

    private GQLSubscriptionPayload getSubscriptionPayload(Stock stock) {
        return new GQLSubscriptionPayload(getNewValue(stock), getOldValue(stock));
    }

    private GQLStock getNewValue(Stock stock) {
        GQLStock gqlStock = new GQLStock(stock.getId(), stock.getName(), stock.getCurrentPrice(),
            stock.getLastUpdate());
        gqlStock.setPriceHistory(getPriceHistories(stock).stream()
            .map(p -> new GQLPriceHistory(stock.getId(), p.getPrice(), p.getTimestamp()))
            .collect(Collectors.toList()));
        return gqlStock;
    }

    private GQLStock getOldValue(Stock stock) {
        List<PriceHistory> priceHistoryList = getPriceHistories(stock);
        if (priceHistoryList == null) {
            return null;
        } else {
            PriceHistory priceHistory = priceHistoryList.remove(FIRST_INDEX);
            GQLStock gqlStock = new GQLStock(stock.getId(), stock.getName(),
                priceHistory.getPrice(), priceHistory.getTimestamp());
            gqlStock.setPriceHistory(priceHistoryList.stream()
                .map(p -> new GQLPriceHistory(stock.getId(), p.getPrice(), p.getTimestamp()))
                .collect(Collectors.toList()));
            return gqlStock;
        }
    }

    private List<PriceHistory> getPriceHistories(Stock stock) {
        return priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(stock.getId());
    }

    private void emitSubscriptionPayload(ObservableEmitter<GQLSubscriptionPayload> emitter,
        GQLSubscriptionPayload lastStock) {
        try {
            emitter.onNext(lastStock);
        } catch (RuntimeException rte) {
            rte.printStackTrace();
        }
    }

    public Flowable<GQLSubscriptionPayload> getPublisher() {
        return publisher;
    }
}
