import { Injectable } from '@angular/core';
import { Stock } from './stock';
import {Observable} from 'rxjs/Observable';
import {ApiService} from './api.service';
import {Price} from './price';

@Injectable()
export class StockService {

  constructor(private apiService: ApiService) { }

  createStock(stock: Stock): Observable<Stock> {
    return this.apiService.createStock(stock);
  }

  updateStockPrice(stockId: number, newPrice: number): Observable<Stock> {
    let stock = new Stock({
      id: stockId,
      currentPrice: newPrice
    });
    return this.apiService.updateStockPrice(stock);
  }

  getAllStocks(): Observable<Stock[]> {
    return this.apiService.getAllStocks();
  }

  getPriceHistoryByStockId(stockId: number): Observable<Price[]> {
    return this.apiService.getPriceHistoryByStockId(stockId);
  }
}
