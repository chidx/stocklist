import { TestBed, inject } from '@angular/core/testing';

import { StockService } from './stock.service';
import { Stock } from './stock';

describe('StockService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StockService]
    });
  });

  it('should be created', inject([StockService], (service: StockService) => {
    expect(service).toBeTruthy();
  }));

  describe('#getAllStocks', () => {
    it('should return an empty array by default',
      inject([StockService], (service: StockService) => {
        expect(service.getAllStocks()).toEqual([]);
      }));
  });

  describe('#getPriceHistoryByStockId', () => {
    it('should return an empty array by default',
      inject([StockService], (service: StockService) => {
        expect(service.getPriceHistoryByStockId(1)).toEqual([]);
      }));
  });

  describe('#createStock', () => {
    it('should return a Stock by default',
      inject([StockService], (service: StockService) => {
        const stock = new Stock({
          name: 'GOGL',
          currentPrice: 90.34,
          lastUpdate: 2308248358945
        });
        expect(service.createStock(new Stock())).toEqual(stock);
      }));
  });

  describe('#updateStock', () => {
    it('should return a Stock by default',
      inject([StockService], (service: StockService) => {
        const stock = new Stock({
          name: 'GOGL',
          currentPrice: 90.34,
          lastUpdate: 2308248358945
        });
        expect(service.updateStockPrice(1, 90)).toEqual(stock);
      }));
  });
});
