package com.assignment.stocklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableWebSocket
public class StocklistApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StocklistApplication.class, args);
	}
}
