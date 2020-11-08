package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.helpers.JsonUtils;
import bgu.spl.mics.application.helpers.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MoneyRegisterTest {
    private static MoneyRegister moneyRegister;
    private static String orderReceiptsFilePath;
    private static String moneyRegisterFilePath;
    private static Customer[] customers;
    private static BookInventoryInfo[] books;
    private static LinkedList<OrderReceipt> orderReceipts;

    @BeforeClass
    public static void beforeClass() throws Exception {
        moneyRegister = MoneyRegister.getInstance();
        orderReceiptsFilePath = "src/test/java/bgu/spl/mics/output/orderReceipts.ser";
        moneyRegisterFilePath = "src/test/java/bgu/spl/mics/output/moneyRegister.ser";
        String customersConfigFilePath = "src/test/java/bgu/spl/mics/input/customers.json";
        String booksConfigFilePath = "src/test/java/bgu/spl/mics/input/books.json";

        customers = JsonUtils.deserializeJsonToObj(FileUtils.readFileToString(new File(customersConfigFilePath),
                Charset.defaultCharset()), Customer[].class);
        books = JsonUtils.deserializeJsonToObj(FileUtils.readFileToString(new File(booksConfigFilePath),
                Charset.defaultCharset()), BookInventoryInfo[].class);

        orderReceipts = new LinkedList<>();

        // Adding receipts to the money register and charging customers
        int i = 0;
        for (BookInventoryInfo book : books) {
            for (Customer customer : customers) {
                OrderReceipt orderReceipt = new OrderReceipt(i++, "a", customer.getId(), book.getBookTitle(),
                        book.getPrice(), 0, 0, 0);
                orderReceipts.add(orderReceipt);
                moneyRegister.file(orderReceipt);
                moneyRegister.chargeCreditCard(customer, book.getPrice());
            }
        }
    }

    @Before
    public void before() {
    }

    /**
     * It should verify the total earnings of the store
     */
    @Test
    public void verifyTotalEarnings() {
        int totalSum = 0;
        for (BookInventoryInfo book : books) {
            totalSum += book.getPrice();
        }
        assertEquals(totalSum * customers.length, moneyRegister.getTotalEarnings());
    }

    /**
     * It should serialize and deserialize the orders receipts
     */
    @Test
    public void verifySerializeOrderReceipts() {
        moneyRegister.printOrderReceipts(orderReceiptsFilePath);
        List<OrderReceipt> orders = SerializationUtils.deserializeObject(orderReceiptsFilePath);
        assertNotNull(orders);

        int i = 0;

        for (OrderReceipt receipt: orders){
            assertEquals(receipt.getOrderId(), orderReceipts.get(i++).getOrderId());
        }
    }

    /**
     * It should serialize and deserialize the Money Register
     */
    @Test
    public void verifySerializeMoneyRegister() {
        SerializationUtils.serializeObject(moneyRegister, moneyRegisterFilePath);
        MoneyRegister moneyRegisterDeserialize = SerializationUtils.deserializeObject(moneyRegisterFilePath);

        assertNotNull(moneyRegisterDeserialize);
        assertEquals(moneyRegister.getTotalEarnings(), moneyRegisterDeserialize.getTotalEarnings());
    }

    @After
    public void after() throws Exception {
        FileUtils.deleteQuietly(new File(orderReceiptsFilePath));
        FileUtils.deleteQuietly(new File(moneyRegisterFilePath));
    }
}
