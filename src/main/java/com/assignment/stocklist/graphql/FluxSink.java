package com.assignment.stocklist.graphql;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FluxSink {

    private final Map<String, reactor.core.publisher.FluxSink<GQLSubscriptionPayload>>
        sinks = new ConcurrentHashMap<>();

    public FluxSink() {
    }

    public Map<String, reactor.core.publisher.FluxSink<GQLSubscriptionPayload>> getSinks() {
        return sinks;
    }
}
