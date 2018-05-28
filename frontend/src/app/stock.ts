import {Price} from './price';

export class Stock {
    id: number;
    name: string;
    prices: Price[];
    priceHistory: Price[];
    currentPrice: number;
    lastUpdate: number;
    lastUpdateAt: string;

    constructor(values: Object= {}) {
        Object.assign(this, values);
    }
}
