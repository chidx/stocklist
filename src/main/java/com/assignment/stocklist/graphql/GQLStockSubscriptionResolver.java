package com.assignment.stocklist.graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class GQLStockSubscriptionResolver implements GraphQLSubscriptionResolver {

    private static final String STOCK_ADDED = "STOCK_ADDED";
    private static final String PRICE_UPDATED = "PRICE_UPDATED";
    private static final String ALL_UPDATES = "ALL_UPDATES";
    private final FluxSink flux;

    public Publisher<GQLSubscriptionPayload> stockSubscription(String statusEnum) {
        log.info("Incoming GraphQL subscription coming...");
        if (StatusEnum.CREATED.toString().equals(statusEnum)) {
            return Flux.create(fluxSink -> flux.getSinks().put(STOCK_ADDED, fluxSink));
        } else if (StatusEnum.UPDATED.toString().equals(statusEnum)) {
            return Flux.create(fluxSink -> flux.getSinks().put(PRICE_UPDATED, fluxSink));
        } else {
            return Flux.create(fluxSink -> flux.getSinks().put(ALL_UPDATES, fluxSink));
        }
    }

}
