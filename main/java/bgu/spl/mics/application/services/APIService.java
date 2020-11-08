package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
    private Customer customer;
    private CountDownLatch latch;

    public APIService(String name, Customer customer, CountDownLatch latch) {
        super(name);
        this.customer = customer;
        this.latch = latch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, callback -> terminate());
        subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {

            for (OrderSchedule orderSchedule : customer.getOrderSchedulesByTick(tickBroadcast.getCurrentTick())) {
                Future<OrderReceipt> future = sendEvent(new OrderBookEvent(customer, orderSchedule));
                if (future != null && future.get() != null) {
                    customer.addReceipt(future.get());
                }
            }
        });
        latch.countDown();
    }
}
