package best.purchase.models;

import java.math.BigDecimal;
import best.purchase.interfaces.Order;
import best.purchase.interfaces.Quote;

public class Quote2015v1 implements Quote {

    private int quantity;
    private BigDecimal price;

    public Quote2015v1(int quantity, BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

}

