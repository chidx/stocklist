import { Injectable } from '@angular/core';
import { Stock } from './stock';
import {Observable} from 'rxjs/Observable';
import {ApiService} from './api.service';
import {Price} from './price';
import { Apollo } from 'apollo-angular';
import { ALL_STOCKS_QUERY } from './graphql';
import { Subscription } from 'apollo-client/util/Observable';

@Injectable()
export class StockService {

  constructor(private apiService: ApiService, private apollo: Apollo) { }

  createStock(stock: Stock): Observable<Stock> {
    return this.apiService.createStock(stock);
  }

  updateStockPrice(stockId: number, newPrice: number): Observable<Stock> {
    const stock = new Stock({
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

  createStockGQL(stock: Stock): Observable<Stock> {
    return null;
  }

  updateStockPriceGQL(stockId: number, newPrice: number): Observable<Stock> {
    return null;
  }

  getAllStocksGQL(): Subscription {
    return this.apollo.watchQuery({
      query: ALL_STOCKS_QUERY
    }).valueChanges.subscribe((response) => {
      return response.data;
    });
  }

  getPriceHistoryByStockIdGQL(stockId: number): Observable<Price[]> {
    return null;
  }
}
