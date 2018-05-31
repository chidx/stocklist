package com.assignment.stocklist.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GQLStockMutationResolver implements GraphQLMutationResolver {

    private final GQLStockService stockService;

    public GQLStock addStock(String name, Float price) {
        return stockService.addStock(name, price);
    }

    public GQLStock updatePrice(String id, Float price) {
        return stockService.updateStockPrice(id, price);
    }
}
