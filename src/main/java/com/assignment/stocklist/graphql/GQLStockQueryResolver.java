package com.assignment.stocklist.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GQLStockQueryResolver implements GraphQLQueryResolver {

    private final GQLStockService stockService;
    private final UserDetailsService userDetailsService;

    public List<GQLStock> stocks() {
        log.info("get stocks");
        return stockService.getAllStocks();
    }

    public GQLStock stock(String name) {
        log.info("get stock with name " + name);
        return stockService.getStock(name);
    }

    public String login(GQLCredentialsInput credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "sessionID";
    }
}
