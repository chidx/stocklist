package com.assignment.stocklist.controller;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StockController {

    private StockRepository stockRepository;
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public StockController(StockRepository stockRepository,
        PriceHistoryRepository priceHistoryRepository) {
        this.stockRepository = stockRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok((List<Stock>) stockRepository.findAll());
    }

    @GetMapping("/prices/{stockId}")
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable("stockId") Long stockId) {
        Optional<Stock> maybeStock = stockRepository.findById(stockId);
        return maybeStock.map(stock -> {
            List<PriceHistory> stockList = new ArrayList<>();
            stockList.add(new PriceHistory(stock.getId(), stock.getCurrentPrice(), stock.getLastUpdate()));
            stockList.addAll(
                priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(stock.getId()));
            return ResponseEntity.ok(stockList);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stocks/{id}")
    public ResponseEntity<Stock> getStock(@PathVariable("id") Long stockId) {
        Optional<Stock> maybeStock = stockRepository.findById(stockId);
        return maybeStock.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/stocks/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable("id") Long stockId,
        @RequestBody Stock updatedStock) {
        if (updatedStock.getCurrentPrice() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Stock> maybeStock = stockRepository.findById(stockId);
        return maybeStock.map(stock -> {
            PriceHistory priceHistory =
                new PriceHistory(stock.getId(), stock.getCurrentPrice(), stock.getLastUpdate());
            priceHistoryRepository.save(priceHistory);
            stock.setCurrentPrice(updatedStock.getCurrentPrice());
            stock.setLastUpdate(System.currentTimeMillis());
            Stock saved = stockRepository.save(stock);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/stocks")
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        if (stock.getName() == null || stock.getCurrentPrice() == null) {
            return ResponseEntity.badRequest().build();
        }
        stock.setLastUpdate(System.currentTimeMillis());
        Stock newStock = stockRepository.save(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }
}
