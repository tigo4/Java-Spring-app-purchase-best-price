package best.purchase.interfaces;

import java.util.List;

public interface BuyingMachine {

    int purchase(int quantity);

    void init(List<Merchant> merchants);

    void init(List<Merchant> merchants, int failQuoteAt, int failOrderAt);

    void init(List<Merchant> merchants, boolean failAllOrders);

    List<Merchant> getMerchants();

}

