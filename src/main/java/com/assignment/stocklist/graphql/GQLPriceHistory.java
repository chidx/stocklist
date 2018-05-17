package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;

import io.leangen.graphql.annotations.GraphQLQuery;

public class GQLPriceHistory extends PriceHistory {

    public GQLPriceHistory(Long stockId, Float price, Long timestamp) {
        super(stockId, price, timestamp);
    }

    @Override
    @GraphQLQuery(name = "id", description = "Price history id")
    public Long getId() {
        return super.getId();
    }

    @Override
    @GraphQLQuery(name = "stockId", description = "Stock Id")
    public Long getStockId() {
        return super.getStockId();
    }

    @Override
    @GraphQLQuery(name = "price", description = "Price")
    public Float getPrice() {
        return super.getPrice();
    }

    @GraphQLQuery(name = "timestampAt", description = "Timestamp")
    public String getTimestampAt() {
        return super.getTimestamp().toString();
    }
}
