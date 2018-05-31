package com.assignment.stocklist.graphql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GQLCredentialsInput {

    private final String username;
    private final String password;
}
