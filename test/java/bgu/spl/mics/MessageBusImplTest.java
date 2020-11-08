package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.*;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private static MicroService mockUpService;
    private static MicroService mockUpBroadCastService;
    private static ExampleEvent mockUpEvent;
    private static Broadcast mockUpBroadCastEvent;

    private static MessageBus messageBus;

    @BeforeClass
    public static void beforeClass() {
        // Creating MockUps services
        mockUpService = new ExampleEventHandlerService("ev-handle", new String[]{"10"});
        mockUpBroadCastService = new ExampleBroadcastListenerService("brod-listener", new String[]{"10"});

        // Creating MockUps events
        mockUpEvent = new ExampleEvent(mockUpService.getName());
        mockUpBroadCastEvent = new ExampleBroadcast(mockUpBroadCastService.getName());
        messageBus = MessageBusImpl.getInstance();
    }

    @Before
    public void before() {
    }

    /**
     * It should verify that the micro service has been successfully registered to the message bus and ready to
     * take messages.
     */
    @Test
    public void verifyRegister() {
        //an effectively final
        String[] error = new String[1];
        messageBus.register(mockUpService);

        // Checking if the micro-service can take messages from its allocated queue
        Thread t = new Thread(() -> {
            try {
                messageBus.awaitMessage(mockUpService);
            } catch (IllegalStateException e) {
                error[0] = e.toString();
            } catch (InterruptedException e) {
                // Other error has occurred that is unrelated to the registration operation
            }
        });
        t.start();

        try {
            // Notice we are not subscribing the micro service to messages as a result we should end
            // the thread work so it wont block the test
            t.interrupt();
            t.join();
        } catch (InterruptedException e) {
            // Deliberately interrupted
        }

        assertNull(error[0]);
    }

    /**
     * It should verify that a registered micro service is listening to its subscribed events
     */
    @Test
    public void verifySubscribeEvent() {
        messageBus.register(mockUpService);
        messageBus.subscribeEvent(ExampleEvent.class, mockUpService);
        Future<?> future = messageBus.sendEvent(mockUpEvent);
        assertNotNull("Null should be returned only in case no micro-service has subscribed to an event", future);
    }

    /**
     * It should verify that a micro service was able to get the sent event he was subscribed to
     */
    @Test
    public void verifySendEvent() {
        //an effectively final
        String[] error = new String[1];
        messageBus.register(mockUpService);
        messageBus.subscribeEvent(ExampleEvent.class, mockUpService);

        // Checking if the micro-service can receive its subscribed message
        Thread t = new Thread(() -> {
            try {
                messageBus.awaitMessage(mockUpService);
            } catch (Exception e) {
                error[0] = e.toString();
            }
        });
        t.start();

        Future<?> future = messageBus.sendEvent(mockUpEvent);

        try {
            t.join();
        } catch (InterruptedException e) {
            error[0] = e.toString();
        }

        messageBus.complete(mockUpEvent, "done");
        assertEquals(true, future.isDone());
    }

    /**
     * It should verify that a registered micro service is listening to its subscribed broadcast events
     */
    @Test
    public void verifySubscribeBroadcast() {
        messageBus.register(mockUpBroadCastService);
        messageBus.subscribeBroadcast(mockUpBroadCastEvent.getClass(), mockUpBroadCastService);
        messageBus.sendBroadcast(mockUpBroadCastEvent);
        try {
            Message sentMessage = messageBus.awaitMessage(mockUpBroadCastService);
            assertNotNull(sentMessage);
        } catch (IllegalStateException | InterruptedException e) {
            fail("The micro service could not get the broadcast message");
        }
    }

    /**
     * It should verify that the message bus has successfully resolved the future associated with the completed event
     */
    @Test
    public void verifyComplete() {
        messageBus.register(mockUpService);
        messageBus.subscribeEvent(ExampleEvent.class, mockUpService);
        Future<String> future = messageBus.sendEvent(mockUpEvent);
        messageBus.complete(mockUpEvent, "done");

        // Verifying that the future got resolved by the 'complete' operation
        assertEquals(true, future.isDone());
    }

    /**
     * It should verify that a micro service has been successfully unregistered and is not listening to messages any more
     */
    @Test
    public void verifyUnregister() {
        messageBus.register(mockUpService);
        messageBus.subscribeEvent(ExampleEvent.class, mockUpService);
        messageBus.unregister(mockUpService);
        Future<String> future = messageBus.sendEvent(mockUpEvent);

        // Verifying returned future is null because we have unregistered the only micro-service that was subscribed to ExampleEvent
        assertNull(future);
    }

    @After
    public void after() {
    }

    @AfterClass
    public static void afterClass() {
    }
}
