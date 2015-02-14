package best.purchase.interfaces;

import java.util.List;

public interface BuyingMachine {

    int purchase(int quantity);

    List<Merchant> getMerchants();

}

