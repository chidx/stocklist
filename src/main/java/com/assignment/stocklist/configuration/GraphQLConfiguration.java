package com.assignment.stocklist.configuration;

import com.assignment.stocklist.graphql.GQLService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;

@Configuration
public class GraphQLConfiguration implements WebSocketConfigurer {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024 * 1024);
        container.setMaxSessionIdleTimeout(30L * 1000L);
        return container;
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new PerConnectionWebSocketHandler(SubscriptionWebSocketHandler.class);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/subscriptions").setAllowedOrigins("*").withSockJS();
    }

    @Bean
    @Autowired
    public GraphQLSchema graphQLSchema(GQLService gqlService) {
        return new GraphQLSchemaGenerator()
            .withResolverBuilders(
                //Resolve by annotations
                new AnnotatedResolverBuilder(),
                //Resolve public methods inside root package
                new PublicResolverBuilder("com.assignment.stocklist"))
            .withOperationsFromSingleton(gqlService)
            .withValueMapperFactory(new JacksonValueMapperFactory())
            .generate();
    }


    @Bean
    public WebMvcConfigurer forwardToIndex() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName(
                    "forward:/graphiql/index.html");
                registry.addViewController("/subscription-example").setViewName(
                    "forward:/websocket/index.html"
                );
            }
        };
    }
}
