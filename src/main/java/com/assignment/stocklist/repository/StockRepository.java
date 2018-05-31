package com.assignment.stocklist.repository;

import com.assignment.stocklist.model.Stock;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long> {

    Optional<Stock> findByName(String name);

    Optional<Stock> findById(Long id);
}
