import { Stock } from './stock';

export class SubscriptionPayload {
    previousValue: Stock;
    newValue: Stock;

    constructor(values: Object= {}) {
        Object.assign(this, values);
    }
}
