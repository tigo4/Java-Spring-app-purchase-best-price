package best.purchase;

import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.util.List;

public class AsyncBuyingMachine implements BuyingMachine {

    List<Merchant> merchants;

    AsyncBuyingMachine(List<Merchant> merchants) {
        this.merchants = merchants;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public int purchase(int quantity) {

        int purchased = 0;

        if (quantity == 0 || merchants == null || merchants.size() == 0) return 0;

        //...
        //quote...
        //order...
        Order order;
        try {
            Quote quote = merchants.get(0).quote();
            order = new Order2015v1(quantity,quote); 
        } catch (Exception e) {
            return 0;
        }

        try {
            OrderResponse orderResponse = merchants.get(0).order(order);
            purchased = orderResponse.getQuantity();
        } catch (Exception e) {
            return 0;
        }

        return purchased;

    }

}

