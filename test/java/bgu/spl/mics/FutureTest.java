package bgu.spl.mics;

import org.junit.*;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class FutureTest {
    private static String expectedResolvedValue;
    private Future<String> future;

    @BeforeClass
    public static void beforeClass() {
        expectedResolvedValue = "done";
    }

    @Before
    public void before() {
        // Before each test clearing the future object state (reinitializing it)
        future = new Future<>();
    }

    /**
     * It should make a thread to wait for the future to be resolved and then return the resolved value of the future object
     */
    @Test
    public void retrieveFutureResultsTest() {
        //an effectively final
        String[] resolvedValue = new String[1];
        Thread t = new Thread(() -> {
            resolvedValue[0] = future.get();
        });
        t.start();
        future.resolve(expectedResolvedValue);
        try {
            t.join();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertEquals(expectedResolvedValue, resolvedValue[0]);
    }

    /**
     * It should mark the future as done when it gets resolved
     */
    @Test
    public void verifyMarkedAsDone() {
        future.resolve(expectedResolvedValue);
        assertEquals(true, future.isDone());
    }

    /**
     * It should not mark the future as done when it has not yet been resolved
     */
    @Test
    public void verifyNotMarkedAsDone() {
        assertEquals(false, future.isDone());
    }

    /**
     * It should try to get the future results but reach the 'get' operation timeout as the future did not
     * got resolved on the given amount of time
     * WARN: Interfering this test at run time (e.g. Debugging) may result in untrusted test's results.
     * WARN: Test as written below may slow the test suite run time when running tests in serial order
     */
    @Test
    public void verifyRetrieveTimeout() {
        int expectedTimeOutInSeconds = 10;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expectedTimeOutInSeconds);
        future.get(expectedTimeOutInSeconds, TimeUnit.SECONDS);
        assertEquals("Retrieving the future results didn't reach the expected timeout",
                true, Calendar.getInstance().after(calendar));
    }

    /**
     * It should return a null value from the 'get' operation as the future did not
     * got resolved on the given amount of time
     * WARN: Test as written below may slow the test suite run time when running tests in serial order
     */
    @Test
    public void verifyRetrieveTimeoutResultIsNotAvailable() {
        String expectedResults = future.get(5, TimeUnit.SECONDS);
        assertNull(expectedResults);
    }

    /**
     * I should get a real value because the future got resolved before timeout has reached
     * WARN: Test as written below may slow the test suite run time when running tests in serial order
     */
    @Test
    public void verifyRetrieveTimeoutResultAvailable() {
        //an effectively final
        String[] resolvedValue = new String[1];
        Thread t = new Thread(() -> {
            resolvedValue[0] = future.get(5, TimeUnit.SECONDS);
        });
        t.start();
        try {
            Thread.sleep(2000);
            future.resolve(expectedResolvedValue);
            t.join();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertEquals(expectedResolvedValue, resolvedValue[0]);
    }

    @After
    public void after() {
    }

    @AfterClass
    public static void afterClass() {
    }
}
