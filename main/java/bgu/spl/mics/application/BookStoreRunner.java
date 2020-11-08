package bgu.spl.mics.application;

import bgu.spl.mics.application.helpers.JsonUtils;
import bgu.spl.mics.application.helpers.SerializationUtils;
import bgu.spl.mics.application.passiveObjects.BookStoreManager;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    private static final Logger logger = LogManager.getLogger(BookStoreRunner.class.getSimpleName());

    public static void main(String[] args) {
        // Validating program arguments
        BookStoreRunner.validateArguments(args);

        final String API_SERVICE_PREFIX = "api ";
        final String SELLING_SERVICE_PREFIX = "selling ";
        final String LOGISTICS_SERVICE_PREFIX = "logistics ";
        final String RESOURCE_SERVICE_PREFIX = "resource ";
        final String INVENTORY_SERVICE_PREFIX = "inventory ";
        ExecutorService executorService;
        boolean isDone = false;

        try {
            BookStoreManager bookStoreManager = JsonUtils.deserializeJsonToObj(FileUtils.readFileToString(
                    new File(args[0]), Charset.defaultCharset()), BookStoreManager.class);
            CountDownLatch latch = new CountDownLatch(bookStoreManager.getNumberOfServices() - 1);
            executorService = Executors.newFixedThreadPool(bookStoreManager.getNumberOfServices());

            // Loading resources and inventory
            Inventory.getInstance().load(bookStoreManager.getInitialInventory());
            ResourcesHolder.getInstance().load(bookStoreManager.getInitialResources()[0].getVehicles());

            for (int i = 0; i < bookStoreManager.getServices().getCustomers().length; i++) {
                bookStoreManager.getServices().getCustomers()[i].mapTickToOrderSchedule();
                executorService.execute(new APIService(API_SERVICE_PREFIX + (i + 1),
                        bookStoreManager.getServices().getCustomers()[i], latch));
            }
            for (int i = 1; i <= bookStoreManager.getServices().getSelling(); i++) {
                executorService.execute(new SellingService(SELLING_SERVICE_PREFIX + i, latch));
            }
            for (int i = 1; i <= bookStoreManager.getServices().getLogistics(); i++) {
                executorService.execute(new LogisticsService(LOGISTICS_SERVICE_PREFIX + i, latch));
            }
            for (int i = 1; i <= bookStoreManager.getServices().getResourcesService(); i++) {
                executorService.execute(new ResourceService(RESOURCE_SERVICE_PREFIX + i, latch));
            }
            for (int i = 1; i <= bookStoreManager.getServices().getInventoryService(); i++) {
                executorService.execute(new InventoryService(INVENTORY_SERVICE_PREFIX + i, latch));
            }

            executorService.execute(new TimeService(bookStoreManager.getServices().getTime(), latch));

            executorService.shutdown();

            try {
                isDone = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {

            }
            if (isDone) {
                BookStoreRunner.generateOutput(bookStoreManager, args[1], args[2], args[3], args[4]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateOutput(BookStoreManager bookStoreManager, String customersOutputPath, String inventoryOutputPath,
                                       String orderReceiptOutputPath, String moneyRegisterOutputPath) {
        SerializationUtils.serializeObject(bookStoreManager.getCustomersAsHashMap(), customersOutputPath);
        Inventory.getInstance().printInventoryToFile(inventoryOutputPath);
        MoneyRegister.getInstance().printOrderReceipts(orderReceiptOutputPath);
        SerializationUtils.serializeObject(MoneyRegister.getInstance(), moneyRegisterOutputPath);
    }

    private static void validateArguments(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException("\nThe following argument are mandatory:\n1- Json input file to read\n" +
                    "2- The output file for the customers HashMap\n3- The books HashMap object\n" +
                    "4- The list of order receipts\n5- The MoneyRegister object");
        }

        File f = new File(args[0]);
        if (!f.exists())
            logger.error("Invalid input file path: " + args[0]);
    }
}
