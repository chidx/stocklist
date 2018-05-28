import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ApiService} from './api.service';
import {AppRoutingModule} from './app-routing.module';
import {StockService} from './stock.service';
import {StockListComponent} from './stock-list/stock-list.component';
import {CommonModule} from '@angular/common';
import { GraphQLModule } from './apollo.config';


@NgModule({
  declarations: [
    AppComponent,
    StockListComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    GraphQLModule,
    CommonModule
  ],
  providers: [StockService, ApiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
