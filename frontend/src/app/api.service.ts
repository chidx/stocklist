import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Stock} from './stock';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Price} from './price';

const API_URL = environment.apiUrl;
const API_USERNAME = environment.apiUsername;
const API_PASSWORD = environment.apiPassword;

@Injectable()
export class ApiService {

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json',
      'Authorization': 'Basic ' + btoa(API_USERNAME + ':' + API_PASSWORD)})
  };

  constructor(private httpClient: HttpClient) { }

  private handleError (error: Response | any) {
    console.error('ApiService::handleError', error);
    return Observable.throw(error);
  }

  public getAllStocks(): Observable<Stock[]> {
    return this.httpClient
      .get<Stock[]>(API_URL + '/api/stocks', this.httpOptions)
      .catch(this.handleError);
  }

  public getPriceHistoryByStockId(stockId: number): Observable<Price[]> {
    return this.httpClient
      .get<Price[]>(API_URL + '/api/prices/' + stockId, this.httpOptions)
      .catch(this.handleError);
  }

  public createStock(stock: Stock): Observable<Stock> {
    return this.httpClient
      .post(API_URL + '/api/stocks', stock, this.httpOptions)
      .catch(this.handleError);
  }

  public updateStockPrice(stock: Stock): Observable<Stock> {
    return this.httpClient
      .put(API_URL + '/api/stocks/' + stock.id, stock, this.httpOptions)
      .catch(this.handleError);
  }
}
