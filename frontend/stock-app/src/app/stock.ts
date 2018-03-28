import {Price} from './price';

export class Stock {
    id: number;
    name: string;
    prices: Price[];
    currentPrice: number;
    lastUpdate: number;

    constructor(values: Object= {}) {
        Object.assign(this, values);
    }
}
