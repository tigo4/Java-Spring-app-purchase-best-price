package best.purchase;

import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncBuyingMachine implements BuyingMachine {

    private static final Logger logger = LogManager.getLogger("AsyncBuyingMachine");

    List<Merchant> merchants;
    boolean failQuote;
    int failOrderAt;
    boolean failAllOrders;

    public void init(List<Merchant> merchants) {
        centralInit(merchants, false, 0, false);
    }
    public void init(List<Merchant> merchants, boolean failQuote, int failOrderAt) {
        centralInit(merchants, failQuote, failOrderAt, false);
    }
    public void init(List<Merchant> merchants, boolean failAllOrders) {
        centralInit(merchants, false, 0, failAllOrders);
    }

    boolean initialized = false;
    private void centralInit(List<Merchant> merchants, boolean failQuote, int failOrderAt, boolean failAllOrders) {
        this.merchants = merchants;
        this.failQuote = failQuote;
        this.failOrderAt = failOrderAt;
        this.failAllOrders = failAllOrders;
        initialized = true;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public int purchase(int quantity) {

        if (!initialized) {
            logger.error("machine not initialized");
            return 0;
        }
        initialized = false;

        int purchased = 0;

        if (quantity == 0 || merchants == null || merchants.size() == 0) return 0;
        Map<Merchant,Quote> map = new HashMap<Merchant,Quote>();
        // done in paralell ...
        List<Future<Map<Merchant,Quote>>> futures = new ArrayList<Future<Map<Merchant,Quote>>>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (final Merchant merchant : merchants) {

            Callable<Map<Merchant,Quote>> worker = new Callable<Map<Merchant,Quote>>() {
                @Override
                public Map<Merchant,Quote> call() throws Exception {

                    try {
                        synchronized (this) {
                            if (failQuote) {
                                failQuote = false;
                                throw new Exception("quote exception simulation");
                            }
                        }
                        Map<Merchant,Quote> threadMap = new HashMap<Merchant,Quote>();
                        Quote quote = merchant.quote();
                        threadMap.put(merchant, quote);
                        return threadMap;
                    } catch (Exception e) {
                        logger.error("thred error1 " + ExceptionUtils.getStackTrace(e));
                        throw e;
                    }

                }
            };
            Future<Map<Merchant,Quote>> submit = executor.submit(worker);
            futures.add(submit);

        }

        executor.shutdown();
        // wait
        while (!executor.isTerminated()) {}

        for (Future<Map<Merchant,Quote>> future : futures) {
            try {
                map.putAll(future.get());
            } catch (InterruptedException e) {
                logger.error("future error1 " + ExceptionUtils.getStackTrace(e));
            } catch (ExecutionException e) {
                logger.error("future error1 " + ExceptionUtils.getStackTrace(e));
            }
        }

        // sort map by quote price
        Map<Merchant,Quote> sortedMap = new TreeMap<Merchant,Quote>(new CustomComparator(map));
        sortedMap.putAll(map);

        Quote quote;
        Order order;
        OrderResponse orderResponse;
        int remaining;
        int orderQuantity;
        int purchasedQuantity = 0;
        int orderCount = 0;
        for (Merchant merchant : sortedMap.keySet()) {

            orderCount++;

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
                if (failAllOrders)
                    throw new Exception("fail all orders exception simulation");
                if (orderCount == failOrderAt) {
                    failOrderAt = 0;
                    throw new Exception("order exception simulation");
                }
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

