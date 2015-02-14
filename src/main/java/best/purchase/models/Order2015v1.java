package best.purchase.models;

import java.math.BigDecimal;
import best.purchase.interfaces.Order;
import best.purchase.interfaces.Quote;

public class Order2015v1 implements Order {

    private int quantity;
    private Quote quote;

    public Order2015v1(int quantity, Quote quote) {
        this.quantity = quantity;
        this.quote = quote;
    }

    public int getQuantity() {
        return quantity;
    }

    public Quote getQuote() {
        return quote;
    }

}

