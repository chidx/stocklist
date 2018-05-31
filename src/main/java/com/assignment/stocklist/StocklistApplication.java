package com.assignment.stocklist;

import com.assignment.stocklist.graphql.GQLErrorAdapter;
import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.GraphQLErrorHandler;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableWebSocket
@Slf4j
public class StocklistApplication {

	public static void main(String[] args) {
		SpringApplication.run(StocklistApplication.class, args);
	}

	@Bean
	public GraphQLErrorHandler errorHandler() {
		return new GraphQLErrorHandler() {
			@Override
			public List<GraphQLError> processErrors(List<GraphQLError> errors) {
				List<GraphQLError> clientErrors = errors.stream()
					.filter(this::isClientError)
					.collect(Collectors.toList());

				List<GraphQLError> serverErrors = errors.stream()
					.filter(e -> !isClientError(e))
					.map(GQLErrorAdapter::new)
					.collect(Collectors.toList());

				List<GraphQLError> e = new ArrayList<>();
				e.addAll(clientErrors);
				e.addAll(serverErrors);
				return e;
			}

			protected boolean isClientError(GraphQLError error) {
				return !(error instanceof ExceptionWhileDataFetching || error instanceof Throwable);
			}
		};
	}

	/*@Bean
	@Profile("!test")
	public CommandLineRunner initStockData(StockRepository stockRepository,
		PriceHistoryRepository priceHistoryRepository) {
		return args -> {
			log.info("Initialize stock and price history data...");
			List<Stock> stockList = new ArrayList<>();
			stockList.add(new Stock("GOGL", 1000F, System.currentTimeMillis()));
			stockList.add(new Stock("AMD", 2000F, System.currentTimeMillis()));
			stockList.add(new Stock("AAPL", 3000F, System.currentTimeMillis()));
			stockList.add(new Stock("TEAM", 4000F, System.currentTimeMillis()));
			stockList.add(new Stock("AUTO", 5000F, System.currentTimeMillis()));
			stockList.add(new Stock("CASA", 6000F, System.currentTimeMillis()));
			stockList.add(new Stock("CSCO", 7000F, System.currentTimeMillis()));
			stockList.add(new Stock("NULL", 8000F, System.currentTimeMillis()));
			stockList.add(new Stock("ASDF", 1400F, System.currentTimeMillis()));
			stockList.add(new Stock("FOUR", 4000F, System.currentTimeMillis()));
			stockRepository.save(stockList);
			stockRepository.findAll().forEach(stock -> {
				Stock saved = changeStockPrice(stockRepository, priceHistoryRepository, stock, 10F);
				Stock saved1 = changeStockPrice(stockRepository, priceHistoryRepository, saved, -20F);
				Stock saved2 = changeStockPrice(stockRepository, priceHistoryRepository, saved1, 30F);
				changeStockPrice(stockRepository, priceHistoryRepository, saved2, -40F);
			});
			log.info("Finished initializing stock and price history data...");
		};
	}

	private Stock changeStockPrice(StockRepository stockRepository,
		PriceHistoryRepository priceHistoryRepository, Stock stock, Float value) {
		priceHistoryRepository.save(
            new PriceHistory(stock.getId(), stock.getCurrentPrice(), stock.getLastUpdate()));
		stock.setCurrentPrice(stock.getCurrentPrice() + value);
		stock.setLastUpdate(System.currentTimeMillis());
		return stockRepository.save(stock);
	}*/
}
