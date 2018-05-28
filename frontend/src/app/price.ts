export class Price {
  price: number;
  timestamp: number;
  timestampAt: string;

  constructor(values: Object= {}) {
    Object.assign(this, values);
  }
}
