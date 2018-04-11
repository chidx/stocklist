package com.assignment.stocklist.repository;

import com.assignment.stocklist.model.PriceHistory;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceHistoryRepository extends CrudRepository<PriceHistory, Long> {

    List<PriceHistory> findAllByStockIdOrderByTimestampDesc(Long stockId);
}
