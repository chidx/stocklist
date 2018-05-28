import { Stock } from './stock';
import gql from 'graphql-tag';

export const ALL_STOCKS_QUERY = gql`
  query {
    stocks {
        id,
        name,
        currentPrice,
        lastUpdateAt,
        priceHistory {
            price,
            timestampAt
        }
    }
}
`;

export const ADD_STOCK_MUTATION = gql`
    mutation addStock($name: String!, $price: Float!) {
        addStock(name: $name, price: $price) {
            id,
            name,
            currentPrice,
            lastUpdateAt,
            priceHistory {
                price,
                timestampAt
            }
        }
    }
`;

export const UPDATE_STOCK_PRICE = gql`
    mutation updatePrice($id: ID!, $price: Float!) {
        updatePrice(id: $id, price: $price) {
            id,
            name,
            currentPrice,
            lastUpdateAt,
            priceHistory {
                price,
                timestampAt
            }
        }
    }
`;

export const COMMENTS_SUBSCRIPTION = gql`
    subscription stockAdded {
        stockAdded {
            previousValue,
            newValue
        }
    }
`;
