package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.PriceHistory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GQLPriceHistory extends PriceHistory {

    private String timestampAt;

    public GQLPriceHistory(Long stockId, Float price, Long timestamp) {
        super(stockId, price, timestamp);
        this.timestampAt = timestamp.toString();
    }
}
