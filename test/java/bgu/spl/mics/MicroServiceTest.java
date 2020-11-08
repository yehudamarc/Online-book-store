package bgu.spl.mics;

import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MicroServiceTest {
//    // Micro services Threads
//    private static Thread eventHandlerThread;
//    private static Thread senderThread;
//
//    // Micro Services
//    private static ExampleEventHandlerService eventHandlerService;
//    private static ExampleMessageSenderService senderService;
//
//    private static String initialMBT = "10";

    @BeforeClass
    public static void beforeClass() {
//        eventHandlerService = new ExampleEventHandlerService("ev-handle", new String[]{initialMBT});
//        senderService = new ExampleMessageSenderService("sender", new String[]{"event"});
//
//        eventHandlerThread = new Thread(eventHandlerService);
//        senderThread = new Thread(senderService);
//
//        eventHandlerThread.start();

    }

    @Before
    public void before() {
    }

    /**
     * Verifying event handler service is able to receive event messages from sender service
     */
    @Test
    public void exampleTest() throws InterruptedException {
//        Thread.currentThread().sleep(2000);
//        senderThread.start();
//        assertEquals(Integer.parseInt(initialMBT) - 1, eventHandlerService.getMbt());
    }

    @After
    public void after() {
    }

    @AfterClass
    public static void afterClass() {
//        eventHandlerService.terminate();
//        senderService.terminate();
    }
}
