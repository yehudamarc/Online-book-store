package bgu.spl.mics;

import bgu.spl.mics.application.BookStoreRunner;
import static org.junit.Assert.assertEquals;

import bgu.spl.mics.application.helpers.JsonUtils;
import bgu.spl.mics.application.helpers.SerializationUtils;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.helpers.BookStoreRunnerHelper;
import bgu.spl.mics.helpers.FilePair;
import bgu.spl.mics.helpers.OutputCompareHelper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RunWith(value = Parameterized.class)
public class BookStoreRunnerTest {
    private static List<String> errors;
    private static String booksOutput;
    private static String customersOutput;
    private static String ordersOutPut;
    private static String moneyRegisterObjOutput;

    @BeforeClass
    public static void beforeClass() throws IOException {
        customersOutput = (new File("src/test/java/bgu/spl/mics/output/customers.ser")).getCanonicalPath();
        booksOutput = (new File("src/test/java/bgu/spl/mics/output/books.ser")).getCanonicalPath();
        ordersOutPut = (new File("src/test/java/bgu/spl/mics/output/orders.ser")).getCanonicalPath();
        moneyRegisterObjOutput = (new File("src/test/java/bgu/spl/mics/output/moneyRegister.ser")).getCanonicalPath();
    }

    @Parameterized.Parameter
    public FilePair filePair;

    @Parameterized.Parameters(name = "Test {0}")
    public static List<FilePair> data() throws IOException {
        return BookStoreRunnerHelper.getInputJsonFilesList();
    }

    @Before
    public void before() {
        errors = new LinkedList<>();
        BookStoreRunner.main(new String[]{filePair.getInput(), customersOutput, booksOutput, ordersOutPut, moneyRegisterObjOutput});
    }


    /**
     * It gets input json and expected output and compare the program actual output to the expected
     */
    @Test
    public void verifyBookStoreManagerInit() throws IOException {
        OutputCompareHelper outputCompareHelper = JsonUtils.deserializeJsonToObj(FileUtils.readFileToString(
                new File(filePair.getOutput()), Charset.defaultCharset()), OutputCompareHelper.class);

        HashMap<String, Integer> inventory = SerializationUtils.deserializeObject(booksOutput);
        List<OrderReceipt> orderReceipts = SerializationUtils.deserializeObject(ordersOutPut);
        MoneyRegister moneyRegister = SerializationUtils.deserializeObject(moneyRegisterObjOutput);
        HashMap<Integer, Customer> customersHashMap = SerializationUtils.deserializeObject(customersOutput);

        // Comparing total earnings
        if (outputCompareHelper.getTotal() != moneyRegister.getTotalEarnings()) {
            errors.add("Money register total earning Expected: " + outputCompareHelper.getTotal() +
                    ", but ACTUAL: " + moneyRegister.getTotalEarnings());
        }

        // Comparing inventory book amount
        for (BookInventoryInfo book : outputCompareHelper.getBooks()) {
            if (book.getAmountInInventory() != inventory.get(book.getBookTitle())) {
                errors.add("Book: " + book.getBookTitle() + ", amount in inventory expected to have left: " + book.getAmountInInventory() +
                        " but ACTUAL: " + inventory.get(book.getBookTitle()));
            }
        }

        // Comparing order receipts size
        if (orderReceipts.size() != outputCompareHelper.getReceipts().length) {
            errors.add("Total order receipts EXPECTED: " + orderReceipts.size() + ", ACTUAL: " +
                    outputCompareHelper.getReceipts().length);
        }


//        boolean found = false;
//        for (OrderReceipt orderReceipt: outputCompareHelper.getReceipts()) {
//            for (OrderReceipt actualOrderReceipt : orderReceipts) {
//                if (orderReceipt.getOrderId() == actualOrderReceipt.getOrderId()) {
//                    found = true;
//                    if (orderReceipt.getCustomerId() != actualOrderReceipt.getCustomerId()) {
//                        errors.add("Order receipt ID: " + orderReceipt.getOrderId() + " EXPECTED customer ID: " +
//                                orderReceipt.getCustomerId() + " ACTUAL: " + actualOrderReceipt.getCustomerId());
//                    }
//                }
//            }
//
//            if (found) {
//                found = false;
//            } else {
//                errors.add("Could not find Order receipt ID: " + orderReceipt.getOrderId());
//            }
//        }

        // Comparing money register object
        for (Customer customer : outputCompareHelper.getCustomers()) {
            if (customer.getCreditCard().getAmount() != customersHashMap.get(customer.getId()).getCreditCard().getAmount()) {
                errors.add("Customer: " + customer.getName() + ", ID: " + customer.getId() + " expected to have left: " +
                        customer.getCreditCard().getAmount() + " in his\\her credit card, but ACTUAL: " +
                        customersHashMap.get(customer.getId()).getCreditCard().getAmount());
            }
        }

        assertEquals(0, errors.size());
    }

    @After
    public void after() {
        if (errors.size() > 0) {
            System.out.println("######## TEST HAVE ERRORS ########");
            for (String error : errors) {
                System.out.println(error);
            }
            System.out.println("##################################");
        }

        FileUtils.deleteQuietly(new File(booksOutput));
        FileUtils.deleteQuietly(new File(customersOutput));
        FileUtils.deleteQuietly(new File(ordersOutPut));
        FileUtils.deleteQuietly(new File(moneyRegisterObjOutput));
    }
}
