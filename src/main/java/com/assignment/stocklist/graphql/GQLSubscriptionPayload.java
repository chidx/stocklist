package com.assignment.stocklist.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GQLSubscriptionPayload {

    private final GQLStock previousValue;
    private final GQLStock newValue;
}