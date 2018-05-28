package com.assignment.stocklist.graphql;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class GQLSubscriptionPayload {

    private GQLStock previousValue;
    private GQLStock newValue;

    @GraphQLQuery(name = "previousValue")
    public GQLStock getPreviousValue() {
        return previousValue;
    }

    @GraphQLQuery(name = "newValue")
    public GQLStock getNewValue() {
        return newValue;
    }
}
