package com.assignment.stocklist.graphql;

import com.coxautodev.graphql.tools.GraphQLResolver;

import org.springframework.stereotype.Component;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GQLStockResolver implements GraphQLResolver<GQLStock> {

    private final GQLStockService stockService;

    public String name(GQLStock stock) {
        return stockService.getName(stock);
    }

    public Float currentPrice(GQLStock stock) {
        return stockService.getCurrentPrice(stock);
    }

    public Long lastUpdate(GQLStock stock) {
        return stockService.getLastUpdate(stock);
    }

    public List<GQLPriceHistory> priceHistory(GQLStock stock) {
        return stockService.getPriceHistory(stock);
    }
}
