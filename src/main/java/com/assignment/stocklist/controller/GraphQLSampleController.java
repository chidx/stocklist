package com.assignment.stocklist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GraphQLSampleController {

    private final GraphQL graphQL;

    @Autowired
    public GraphQLSampleController(GraphQLSchema graphQLSchema) {

        //Schema generated from query classes
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        log.info("Generated GraphQL schema using SPQR");
    }

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Object> indexFromAnnotated(@RequestBody Map<String, Object> request, HttpServletRequest raw) {
        ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput()
            .query((String) request.get("query"))
            .operationName((String) request.get("operationName"))
            .context(raw)
            .build());
        return executionResult.toSpecification();
    }
}
