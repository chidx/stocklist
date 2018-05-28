package com.assignment.stocklist.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent>{

    private final String port;

    @Autowired
    public ApplicationStartupListener(@Value("${server.port}") String port) {
        this.port = port;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application is running on port: {}", this.port);
    }
}
