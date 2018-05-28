package com.assignment.stocklist.graphql;

import java.time.LocalDateTime;

import io.leangen.graphql.annotations.GraphQLQuery;

public class Timer {

    private LocalDateTime localDateTime;

    public Timer() {
        // NO-OP
    }

    public Timer(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @GraphQLQuery(name = "localDateTime")
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
