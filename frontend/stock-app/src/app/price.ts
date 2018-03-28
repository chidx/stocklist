export class Price {
  price: number;
  timestamp: number;

  constructor(values: Object= {}) {
    Object.assign(this, values);
  }
}
