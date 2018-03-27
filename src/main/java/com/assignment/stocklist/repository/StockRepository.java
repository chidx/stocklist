package com.assignment.stocklist.repository;

import com.assignment.stocklist.model.Stock;

import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<Stock, Long> {}
