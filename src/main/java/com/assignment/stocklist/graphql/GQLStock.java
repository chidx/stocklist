package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.Stock;

import java.util.List;

import io.leangen.graphql.annotations.GraphQLQuery;

public class GQLStock extends Stock {

    private List<GQLPriceHistory> priceHistories;

    public GQLStock(Long id, String name, Float currentPrice, Long lastUpdate,
        List<GQLPriceHistory> priceHistories) {
        super(id, name, currentPrice, lastUpdate);
        this.priceHistories = priceHistories;
    }

    @Override
    @GraphQLQuery(name = "id", description = "Stock Id")
    public Long getId() {
        return super.getId();
    }

    @Override
    @GraphQLQuery(name = "name", description = "Stock Name")
    public String getName() {
        return super.getName();
    }

    @Override
    @GraphQLQuery(name = "currentPrice", description = "Current Price")
    public Float getCurrentPrice() {
        return super.getCurrentPrice();
    }

    @GraphQLQuery(name = "lastUpdateAt", description = "Last Update")
    public String getLastUpdateAt() {
        return super.getLastUpdate().toString();
    }

    @GraphQLQuery(name = "priceHistory", description = "Price History")
    public List<GQLPriceHistory> getPriceHistories() {
        return priceHistories;
    }
}
