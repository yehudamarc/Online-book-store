package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.helpers.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private static Inventory inventory;
    private static BookInventoryInfo book1;
    private static BookInventoryInfo book2;
    private static BookInventoryInfo[] stock;
    private static String inventoryFilePath;

    @BeforeClass
    public static void beforeClass() {
        // Initializing
        book1 = new BookInventoryInfo("book1", 1, 10);
        book2 = new BookInventoryInfo("book2", 3, 20);
        stock = new BookInventoryInfo[]{book1, book2};
        inventory = Inventory.getInstance();
        inventory.load(stock);
        inventoryFilePath = "src/test/java/bgu/spl/mics/output/inventory.ser";
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void getInstance() {
        assertEquals(Inventory.getInstance(), inventory);
    }

    @Test
    public void load() {
        //Test: The amountInInventory for each book must reflect the actual amount on the
        //inventory.
        assertEquals(1, book1.getAmountInInventory());
        assertEquals(3, book2.getAmountInInventory());
    }

    @Test
    public void take() {
        //Test: The number of books sold of some book cannot exceed the available number of
        //books on the inventory.
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, inventory.take("book1"));
        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("book1"));
    }

    @Test
    public void checkAvailabilityAndGetPrice() {
        inventory.load(stock);
        inventory.take("book1");
        assertEquals(-1, inventory.checkAvailabiltyAndGetPrice("book1"));
        assertEquals(20, inventory.checkAvailabiltyAndGetPrice("book2"));
    }

    @Test
    public void printInventoryToFile() {
        SerializationUtils.serializeObject(inventory, inventoryFilePath);
        Inventory inventoryDeserialize = SerializationUtils.deserializeObject(inventoryFilePath);

        assertNotNull(inventoryDeserialize);
        assertEquals(10, inventoryDeserialize.checkAvailabiltyAndGetPrice("book1"));
    }
}