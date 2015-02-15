package best.purchase.interfaces;

import java.util.List;

public interface BuyingMachine {

    int purchase(int quantity);

    void init(List<Merchant> merchants);

    void init(List<Merchant> merchants, int failQuoteAt, int failOrderAt);

    List<Merchant> getMerchants();

}

