import { Stock } from './stock';

describe('Stock', () => {
  it('should create an instance', () => {
    expect(new Stock()).toBeTruthy();
  });

  it('should accept values in the contructor', () => {
    const stock = new Stock({
      name: 'GOGL',
      currentPrice: 90.34,
      lastUpdate: 2308248358945
    });
    expect(stock.name).toEqual('GOGL');
    expect(stock.currentPrice).toEqual(90.34);
    expect(stock.lastUpdate).toEqual(2308248358945);
  });
});
