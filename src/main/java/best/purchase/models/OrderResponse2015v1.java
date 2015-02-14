package best.purchase.models;

import best.purchase.interfaces.OrderResponse;

public class OrderResponse2015v1 implements OrderResponse {

    private int quantity;

    public OrderResponse2015v1(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

}

