import { Component, OnInit, OnDestroy } from '@angular/core';
import {StockService} from '../stock.service';
import {Stock} from '../stock';
import {Price} from '../price';
import { Apollo } from 'apollo-angular';
import { ALL_STOCKS_QUERY, ADD_STOCK_MUTATION, UPDATE_STOCK_PRICE } from './../graphql';
import { Subscription } from 'apollo-client/util/Observable';
import { Observable } from 'apollo-link';
import { WebSocketConfig } from '../websocket.config';
import { Subject } from 'rxjs/Subject';
import { sleep } from '../utils';
import { SubscriptionPayload } from '../subscriptionPayload';

@Component({
  selector: 'app-stock-list',
  templateUrl: './stock-list.component.html',
  styleUrls: ['./stock-list.component.css'],
  providers: [StockService, WebSocketConfig]
})
export class StockListComponent implements OnInit, OnDestroy {
  display = 'none';
  selectedDisplay = 'none';
  stock: Stock = new Stock();
  stocks = [];
  selectedStock: Stock;
  selectedPrice: Price = new Price();
  messages: Array<any> = [];
  subscriptionQuery = 'subscription GQLSubscriptionPayload {\
    stockSubscription(status: ALL_UPDATES) {\
    previousValue { id name currentPrice lastUpdateAt priceHistory { price timestampAt } }\
    newValue { id name currentPrice lastUpdateAt priceHistory { price timestampAt } }\
    }\
  }';

  constructor (private stockService: StockService, private apollo: Apollo, private webSocketConfig: WebSocketConfig) {

  }

  ngOnDestroy() {
    this.webSocketConfig.close();
  }

  ngOnInit() {
    // init stock list
    this.apollo.watchQuery({
      query: ALL_STOCKS_QUERY
    }).valueChanges
    .subscribe(({data, loading}) => {
      if (data.hasOwnProperty('stocks')) {
        this.stocks = data['stocks'];
      }
    });

    let arr = [];
    // execute GraphQL subscription
    this.webSocketConfig.getEventListener()
    .map(event => new SubscriptionPayload({
        previousValue: event.data.stockSubscription.previousValue,
        newValue: event.data.stockSubscription.newValue
      }))
    .subscribe(payload => {
      const oldStock = payload.previousValue;
      const newStock = payload.newValue;
      if (oldStock !== null) {
        const index = this.stocks.findIndex((stock) => stock.name === newStock.name);
        arr = this.stocks.slice(0, index).concat(newStock).concat(this.stocks.slice(index + 1));
        this.stocks = arr;
      } else {
        this.stocks = this.stocks.concat(newStock);
      }
    });
    sleep(2000).then(() => {
      this.webSocketConfig.send(this.subscriptionQuery);
    });
  }

  onSelect(selectedStock) {
    this.selectedStock = selectedStock;
  }

  addStock(stock: Stock) {
    this.onCloseHandled();
    this.apollo.mutate({
      mutation: ADD_STOCK_MUTATION,
      variables: {
        name: stock.name,
        price: stock.currentPrice
      }
    });
    this.stock = new Stock();
  }

  updatePrice() {
    this.onPriceCloseHandled();
    this.apollo.mutate({
      mutation: UPDATE_STOCK_PRICE,
      variables: {
        id: this.selectedStock.id,
        price: this.selectedPrice.price
      }
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
