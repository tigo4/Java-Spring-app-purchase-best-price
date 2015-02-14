package best.purchase.interfaces;

public interface Merchant {

    Quote quote() throws Exception;

    OrderResponse order(Order order) throws Exception;

}

