package best.purchase;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.math.BigDecimal;

public class AsyncBuyingMachineTest {

    @Test
    public void helloTest() {

        AsyncBuyingMachine machine;
        int purchased;

        // null merchants
        machine = new AsyncBuyingMachine(null);
        purchased = machine.purchase(11);
        assertTrue(purchased == 0);

        // merchants size = 0
        machine = new AsyncBuyingMachine(new ArrayList<Merchant>());
        purchased = machine.purchase(11);
        assertTrue(purchased == 0);

        // quantity = 0
        Merchant merchant = new VirtualMerchant(null, null);
        List<Merchant> merchants = new ArrayList<Merchant>();
        merchants.add(merchant);
        machine = new AsyncBuyingMachine(merchants);
        purchased = machine.purchase(0);
        assertTrue(purchased == 0);

    }

    @Test
    public void purchaseAllFromOneMerchant() throws Exception {

        Quote quoteA = new Quote2015v1(3, new BigDecimal(1.78));
        OrderResponse orderResponseA = new OrderResponse2015v1(3);
        Quote quoteB = new Quote2015v1(2, new BigDecimal(1.82));
        OrderResponse orderResponseB = new OrderResponse2015v1(2);
        Quote quoteC = new Quote2015v1(6, new BigDecimal(1.84));
        OrderResponse orderResponseC = new OrderResponse2015v1(6);

        Merchant merchantA = new VirtualMerchant(quoteA, orderResponseA);
        Merchant merchantB = new VirtualMerchant(quoteB, orderResponseB);
        Merchant merchantC = new VirtualMerchant(quoteC, orderResponseC);

        List<Merchant> merchants = new ArrayList<Merchant>();
        merchants.add(merchantA);
        merchants.add(merchantB);
        merchants.add(merchantC);

        BuyingMachine machine = new AsyncBuyingMachine(merchants);
        int purchased = machine.purchase(3);

        assertTrue(purchased == 3);

        merchants = machine.getMerchants();

        merchantA = merchants.get(0); 
        assertTrue(merchantA.quote().getQuantity() == 0);

        merchantB = merchants.get(1); 
        assertTrue(merchantB.quote().getQuantity() == 2);

        merchantC = merchants.get(2); 
        assertTrue(merchantC.quote().getQuantity() == 6);

    }

    @Test
    public void purchaseFromTwoMerchants() {
        assertTrue(true);
    }

    @Test
    public void simulateFailuresAndProceed() {
        assertTrue(true);
    }

    @Test
    public void purchaseFromNoMerchantsAsAllFail() {
        assertTrue(true);
    }

}

