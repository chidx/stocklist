import { Component, OnInit } from '@angular/core';
import {StockService} from '../stock.service';
import {Stock} from '../stock';
import {Price} from '../price';

@Component({
  selector: 'app-stock-list',
  templateUrl: './stock-list.component.html',
  styleUrls: ['./stock-list.component.css'],
  providers: [StockService]
})
export class StockListComponent implements OnInit {
  display = 'none';
  selectedDisplay = 'none';
  stock: Stock = new Stock();
  stocks: Stock[] = [];
  selectedStock: Stock;
  selectedPrice: Price = new Price();

  constructor (private stockService: StockService) {
  }

  ngOnInit() {
    this.stockService.getAllStocks()
      .subscribe((stocks) => {
        this.stocks = stocks;
      });
  }

  onSelect(selectedStock) {
    this.selectedStock = selectedStock;
    this.stockService.getPriceHistoryByStockId(this.selectedStock.id)
      .subscribe(prices => {
        this.selectedStock.prices = prices;
      });
  }

  addStock(stock: Stock) {
    this.onCloseHandled();
    this.stockService.createStock(stock)
      .subscribe((newStock) => {
        this.stocks = this.stocks.concat(newStock);
      });
    this.stock = new Stock();
  }

  updatePrice() {
    this.onPriceCloseHandled();
    this.stockService.updateStockPrice(this.selectedStock.id, this.selectedPrice.price)
      .subscribe((updatedStock) => {
        let index = this.stocks.findIndex((stock) => stock.name === updatedStock.name);
        this.stocks[index] = updatedStock;
        this.selectedStock.prices.unshift(new Price({price: updatedStock.currentPrice, timestamp: updatedStock.lastUpdate}))
      });
    this.selectedPrice = new Price();
  }
  openModal() {
    this.display = 'block';
  }

  onCloseHandled() {
    this.display = 'none';
  }

  openPriceModal() {
    this.selectedDisplay = 'block';
  }

  onPriceCloseHandled() {
    this.selectedDisplay = 'none';
  }

}
