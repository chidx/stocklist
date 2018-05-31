package com.assignment.stocklist.graphql;

import com.assignment.stocklist.model.Stock;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GQLStock extends Stock {

    private List<GQLPriceHistory> priceHistory;
    private String lastUpdateAt;

    public GQLStock(Long id, String name, Float currentPrice, Long lastUpdate) {
        super(id, name, currentPrice, lastUpdate);
        this.lastUpdateAt = lastUpdate.toString();
        this.priceHistory = new ArrayList<>();
    }

    public GQLStock(String name, Float currentPrice, Long lastUpdate) {
        super(name, currentPrice, lastUpdate);
        this.lastUpdateAt = lastUpdate.toString();
        this.priceHistory = new ArrayList<>();
    }

    public GQLStock(Long id, String name, Float currentPrice, Long lastUpdate, List<GQLPriceHistory> priceHistory) {
        super(id, name, currentPrice, lastUpdate);
        this.lastUpdateAt = lastUpdate.toString();
        this.priceHistory = priceHistory;
    }

    public List<GQLPriceHistory> getPriceHistories() {
        return priceHistory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getId().toString())
            .append(" " + getName())
            .append(" " + getCurrentPrice().toString())
            .append(" " + getLastUpdateAt());
        getPriceHistories().forEach(price -> {
            sb.append(" " + price.getPrice());
            sb.append(" " + price.getTimestampAt());
        });
        return sb.toString();
    }

}
