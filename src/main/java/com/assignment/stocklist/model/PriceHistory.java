package com.assignment.stocklist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString
public class PriceHistory {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stockId;
    private Float price;
    private Long timestamp;

    public PriceHistory(Long stockId, Float price, Long timestamp) {
        this.stockId = stockId;
        this.price = price;
        this.timestamp = timestamp;
    }
}
