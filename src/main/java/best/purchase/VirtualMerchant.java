package best.purchase;

import best.purchase.interfaces.*;
import best.purchase.models.*;

public class VirtualMerchant implements Merchant {

    private Quote quote;
    private OrderResponse orderResponse;

    VirtualMerchant(Quote quote, OrderResponse orderResponse) {
        this.quote = quote;
        this.orderResponse = orderResponse;
    }

    public Quote quote() throws Exception {
        // connect to merchant endpoint to get quote ...
        // just return injected constructor quote for testing
        return quote;
    }

    public OrderResponse order(Order order) throws Exception {
        // connect to merchant endpoint to order ...
        // just return injected constructor orderResponse for testing
        quote = new Quote2015v1(quote.getQuantity()-order.getQuantity(), quote.getPrice());
        orderResponse = new OrderResponse2015v1(order.getQuantity());
        return orderResponse;
    }

}

