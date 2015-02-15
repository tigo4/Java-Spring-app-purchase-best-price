package best.purchase;

import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
        Quote quote;
        Map<Merchant,Quote> map = new HashMap<Merchant,Quote>();
        // to be done in paralell ...
        for (Merchant merchant : merchants) {

            try {
                quote = merchant.quote();
                map.put(merchant, quote);
            } catch (Exception e) {
                logger.error("error1 " + ExceptionUtils.getStackTrace(e));
                e = null;
                continue;
            }

        }

        // sort map by quote price
        Map<Merchant,Quote> sortedMap = new TreeMap<Merchant,Quote>(new CustomComparator(map));
        sortedMap.putAll(map);

        Order order;
        OrderResponse orderResponse;
        int remaining;
        int orderQuantity;
        int purchasedQuantity = 0;
        for (Merchant merchant : sortedMap.keySet()) {

            remaining = quantity - purchased;

            logger.info("remaining " + remaining);

            if (remaining == 0)
                break;

            try {

                quote = sortedMap.get(merchant);

                if (remaining >= quote.getQuantity())
                    orderQuantity = quote.getQuantity();
                else
                    orderQuantity = remaining;

                logger.info("buying " + orderQuantity + " at price " + quote.getPrice());

                order = new Order2015v1(orderQuantity, quote); 
                orderResponse = merchant.order(order);

                purchasedQuantity = orderResponse.getQuantity();
                logger.info("purchasedQuantity " + purchasedQuantity);
                purchased += purchasedQuantity;

            } catch (Exception e) {
                logger.error("error2 " + ExceptionUtils.getStackTrace(e));
                e = null;
                continue;
            }

        }

        return purchased;

    }

}

class CustomComparator implements Comparator {
 
    Map map;
 
    CustomComparator(Map map) {
        this.map = map;
    }
 
    public int compare(Object key1, Object key2) {
        Quote quote1 = (Quote2015v1) map.get(key1);
        Quote quote2 = (Quote2015v1) map.get(key2);
        Comparable value1 = (Comparable) quote1.getPrice();
        Comparable value2 = (Comparable) quote2.getPrice();
        return value1.compareTo(value2);
    }

}

