package com.assignment.stocklist.controller;

import com.assignment.stocklist.model.PriceHistory;
import com.assignment.stocklist.model.Stock;
import com.assignment.stocklist.repository.PriceHistoryRepository;
import com.assignment.stocklist.repository.StockRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class StockControllerTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final Long STOCK_ID = 1L;
    private static final float STOCK_PRICE = 60.45F;
    private static final String STOCK_NAME = "GOGL";

    @Mock
    private StockRepository stockRepository;
    @Mock
    private PriceHistoryRepository priceHistoryRepository;
    @InjectMocks
    private StockController stockController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.stockController = new StockController(stockRepository, priceHistoryRepository);
    }

    @Test
    public void getAllStocks_returnMany() {
        List<Stock> stockList = new ArrayList<Stock>(){{
            add(new Stock());
            add(new Stock());
        }};

        when(stockRepository.findAll()).thenReturn(stockList);
        ResponseEntity<List<Stock>> responseEntity = stockController.getAllStocks();

        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(TWO, responseEntity.getBody().size());
    }

    @Test
    public void getAllStocks_returnZero() {
        List<Stock> stockList = new ArrayList<>();

        when(stockRepository.findAll()).thenReturn(stockList);
        ResponseEntity<List<Stock>> responseEntity = stockController.getAllStocks();

        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(ZERO, responseEntity.getBody().size());
    }

    @Test
    public void getPriceHistory_returnHistoryMoreThanOne() {
        Stock stock = new Stock(STOCK_ID, STOCK_NAME, STOCK_PRICE, System.currentTimeMillis());
        Optional<Stock> stockOptional = Optional.of(stock);
        List<PriceHistory> priceHistoryList = new ArrayList<PriceHistory>(){{
           add(new PriceHistory());
           add(new PriceHistory());
        }};

        when(stockRepository.findById(STOCK_ID)).thenReturn(stockOptional);
        when(priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(STOCK_ID))
            .thenReturn(priceHistoryList);

        ResponseEntity<List<PriceHistory>> responseEntity = stockController.getPriceHistory(STOCK_ID);
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(THREE, responseEntity.getBody().size());
    }

    @Test
    public void getPriceHistory_returnHistoryOnlyOne() {
        Stock stock = new Stock(STOCK_ID, STOCK_NAME, STOCK_PRICE, System.currentTimeMillis());
        Optional<Stock> stockOptional = Optional.of(stock);
        List<PriceHistory> priceHistoryList = new ArrayList<>();

        when(stockRepository.findById(STOCK_ID)).thenReturn(stockOptional);
        when(priceHistoryRepository.findAllByStockIdOrderByTimestampDesc(STOCK_ID))
            .thenReturn(priceHistoryList);

        ResponseEntity<List<PriceHistory>> responseEntity = stockController.getPriceHistory(STOCK_ID);
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(ONE, responseEntity.getBody().size());
    }

    @Test
    public void getPriceHistory_returnNotFound() {
        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        ResponseEntity<List<PriceHistory>> responseEntity = stockController.getPriceHistory(
            STOCK_ID);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getStock_returnFound() {
        Stock stock = new Stock();
        stock.setId(STOCK_ID);
        Optional<Stock> stockOptional = Optional.of(stock);

        when(stockRepository.findById(STOCK_ID)).thenReturn(stockOptional);

        ResponseEntity<Stock> responseEntity = stockController.getStock(STOCK_ID);
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(STOCK_ID, responseEntity.getBody().getId());
    }

    @Test
    public void getStock_returnNotFound() {
        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        ResponseEntity<Stock> responseEntity = stockController.getStock(STOCK_ID);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void updateStock_returnSuccess() {
        Stock updatedStock = new Stock();
        updatedStock.setCurrentPrice(STOCK_PRICE);
        Stock oldStock = updatedStock;
        oldStock.setCurrentPrice(STOCK_PRICE - 2F);
        Optional<Stock> oldStockOptional = Optional.of(oldStock);
        PriceHistory priceHistory = new PriceHistory();

        when(stockRepository.findById(STOCK_ID)).thenReturn(oldStockOptional);
        when(priceHistoryRepository.save(any(PriceHistory.class))).thenReturn(priceHistory);
        when(stockRepository.save(oldStock)).thenReturn(oldStock);

        ResponseEntity<Stock> responseEntity = stockController.updateStock(STOCK_ID, updatedStock);
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(updatedStock.getCurrentPrice(), responseEntity.getBody().getCurrentPrice());
    }

    @Test
    public void updateStock_returnBadRequest() {
        Stock stock = new Stock();

        ResponseEntity<Stock> responseEntity = stockController.updateStock(STOCK_ID, stock);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void updateStock_returnNotFound() {
        Stock stock = new Stock();
        stock.setCurrentPrice(STOCK_PRICE);

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        ResponseEntity<Stock> responseEntity = stockController.updateStock(STOCK_ID, stock);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void createStock_returnSuccess() {
        Stock stock = new Stock();
        stock.setName(STOCK_NAME);
        stock.setCurrentPrice(STOCK_PRICE);

        Stock savedStock = stock;
        savedStock.setId(STOCK_ID);
        savedStock.setLastUpdate(System.currentTimeMillis());

        when(stockRepository.save(stock)).thenReturn(savedStock);

        ResponseEntity<Stock> responseEntity = stockController.createStock(stock);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(savedStock.getLastUpdate());
    }

    @Test
    public void createStock_nullName_returnBadRequest() {
        Stock stock = new Stock();
        stock.setCurrentPrice(STOCK_PRICE);

        ResponseEntity<Stock> responseEntity = stockController.createStock(stock);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void createStock_nullPrice_returnBadRequest() {
        Stock stock = new Stock();
        stock.setName(STOCK_NAME);

        ResponseEntity<Stock> responseEntity = stockController.createStock(stock);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }
}
