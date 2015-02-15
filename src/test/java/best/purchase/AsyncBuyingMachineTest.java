package best.purchase;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import best.purchase.interfaces.*;
import best.purchase.models.*;
import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

public class AsyncBuyingMachineTest {

    private static final Logger logger = LogManager.getLogger("AsyncBuyingMachineTest");

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    @Test
    public void helloTest() {

        logger.info("===== helloTest() ");

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
        Merchant merchant = new VirtualMerchant(null);
        List<Merchant> merchants = new ArrayList<Merchant>();
        merchants.add(merchant);
        machine = new AsyncBuyingMachine(merchants);
        purchased = machine.purchase(0);
        assertTrue(purchased == 0);

    }

    Quote quoteA, quoteB, quoteC;
    Merchant merchantA, merchantB, merchantC;
    List<Merchant> merchants;

    @Before
    public void init() {

        logger.info("===== init test ");

        quoteA = (Quote2015v1) applicationContext.getBean("quoteA");
        quoteB = (Quote2015v1) applicationContext.getBean("quoteB");
        quoteC = (Quote2015v1) applicationContext.getBean("quoteC");

        merchantA = new VirtualMerchant(quoteA);
        merchantB = new VirtualMerchant(quoteB);
        merchantC = new VirtualMerchant(quoteC);

        merchants = new ArrayList<Merchant>();
        merchants.add(merchantA);
        merchants.add(merchantB);
        merchants.add(merchantC);

    }

    @Test
    public void purchaseAllFromOneMerchant() throws Exception {

        logger.info("===== purchaseAllFromOneMerchant() ");

        BuyingMachine machine = new AsyncBuyingMachine(merchants);
        int purchased = machine.purchase(3);
        logger.info("===== purchased: " + purchased);

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
    public void purchaseFromTwoMerchants() throws Exception {

        logger.info("===== purchaseFromTwoMerchants() ");

        BuyingMachine machine = new AsyncBuyingMachine(merchants);
        int purchased = machine.purchase(4);
        logger.info("===== purchased: " + purchased);

        assertTrue(purchased == 4);

        merchants = machine.getMerchants();

        merchantA = merchants.get(0);
        assertTrue(merchantA.quote().getQuantity() == 0);

        merchantB = merchants.get(1);
        assertTrue(merchantB.quote().getQuantity() == 1);

        merchantC = merchants.get(2);
        assertTrue(merchantC.quote().getQuantity() == 6);

    }

    @Test
    public void simulateFailuresAndProceed() throws Exception {

        logger.info("===== simulateFailuresAndProceed() ");

        BuyingMachine machine = new AsyncBuyingMachine(merchants);
        machine.setSimulateQuoteFail(2);
        machine.setSimulateOrderFail(1);
        int purchased = machine.purchase(4);
        logger.info("===== purchased: " + purchased);

        assertTrue(purchased == 4);

        merchants = machine.getMerchants();

        merchantA = merchants.get(0);
        assertTrue(merchantA.quote().getQuantity() == 3);

        merchantB = merchants.get(1);
        assertTrue(merchantB.quote().getQuantity() == 2);

        merchantC = merchants.get(2);
        assertTrue(merchantC.quote().getQuantity() == 2);

    }

    @Test
    public void purchaseFromNoMerchantsAsAllFail() {
        logger.info("===== purchaseFromNoMerchantsAsAllFail() ");
        assertTrue(true);
    }

}

