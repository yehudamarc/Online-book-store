package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.CreditCard;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

public class CustomerTest {
    private static Customer customer;
    private static int expectedTick;
    private static int expectedOrders;

    @BeforeClass
    public static void beforeClass() {
        customer = new Customer("Tom", 0, "a", 2,
                new CreditCard(0,0), new OrderSchedule[]{new OrderSchedule("a", 2),
                new OrderSchedule("aa", 2)});
        expectedTick = 2;
        expectedOrders = 2;
        customer.mapTickToOrderSchedule();
    }

    @Before
    public void before() {
    }

    /**
     * It should get the correct order scheduled with the given tick
     */
    @Test
    public void retrieveOrdersByTick() {
        LinkedList<OrderSchedule> orderSchedules = customer.getOrderSchedulesByTick(expectedTick);
        assertEquals(expectedOrders, orderSchedules.size());
    }

    /**
     * It should get the correct order scheduled with the given tick
     */
    @Test
    public void retrieveOrdersByTickNull() {
        LinkedList<OrderSchedule> orderSchedules = customer.getOrderSchedulesByTick(expectedTick + 1);
        assertEquals(0, orderSchedules.size());
    }
}
