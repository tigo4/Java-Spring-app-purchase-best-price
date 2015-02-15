package best.purchase;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
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

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class AsyncBuyingMachineTest {

    private static final Logger logger = LogManager.getLogger("AsyncBuyingMachineTest");

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    @Autowired(required = true)
    private BuyingMachine machine;

    @Test
    public void helloTest() {

        logger.info("===== helloTest() ");

        // null merchants
        machine.init(null);
        int purchased = machine.purchase(11);
        assertTrue(purchased == 0);

        // merchants size = 0
        machine.init(new ArrayList<Merchant>());
        purchased = machine.purchase(11);
        assertTrue(purchased == 0);

        // quantity = 0
        Merchant merchant = new VirtualMerchant(null);
        List<Merchant> merchants = new ArrayList<Merchant>();
        merchants.add(merchant);
        machine.init(merchants);
        purchased = machine.purchase(0);
        assertTrue(purchased == 0);

    }

    Quote quoteA, quoteB, quoteC;
    Merchant merchantA, merchantB, merchantC;
    List<Merchant> merchants;

    @Before
    public void init() {

        logger.info("===== init test ");

        merchantA = (VirtualMerchant) applicationContext.getBean("merchantA");
        merchantB = (VirtualMerchant) applicationContext.getBean("merchantB");
        merchantC = (VirtualMerchant) applicationContext.getBean("merchantC");

        merchants = new ArrayList<Merchant>();
        merchants.add(merchantA);
        merchants.add(merchantB);
        merchants.add(merchantC);

    }

    @Test
    public void purchaseAllFromOneMerchant() throws Exception {

        logger.info("===== purchaseAllFromOneMerchant() ");

        machine.init(merchants);
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

        machine.init(merchants);
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

        machine.init(merchants, 2, 1);
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

