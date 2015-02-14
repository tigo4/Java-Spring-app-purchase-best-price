package best.purchase;

import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class AsyncBuyingMachine implements BuyingMachine {

    private static final Logger logger = LogManager.getLogger("AsyncBuyingMachine");

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
            logger.error("error1 " + ExceptionUtils.getStackTrace(e));
            e = null;
            return 0;
        }

        try {
            OrderResponse orderResponse = merchants.get(0).order(order);
            purchased = orderResponse.getQuantity();
        } catch (Exception e) {
            logger.error("error2 " + ExceptionUtils.getStackTrace(e));
            e = null;
            return 0;
        }

        return purchased;

    }

}

