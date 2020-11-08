package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

    private int currentTick;
    private int orderId;
    private MoneyRegister moneyRegister;
    private CountDownLatch latch;

    public SellingService(String name, CountDownLatch latch) {
        super(name);
        this.latch = latch;
        orderId = 0;
        moneyRegister = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, callback -> terminate());
        subscribeBroadcast(TickBroadcast.class, callback -> currentTick = callback.getCurrentTick());
        subscribeEvent(OrderBookEvent.class, callback -> {
            int processTick = currentTick;

            // Check availability
            Future<Integer> checkAvailabilityResult = sendEvent(new CheckAvailability(callback.getOrderSchedule().getBookTitle()));

            // Verifying the future that resolved and check if book available
            if (checkAvailabilityResult == null || checkAvailabilityResult.get() == null || checkAvailabilityResult.get() == -1) {
                complete(callback, null);
                return;
            }
            //synchronizing to prevent another selling service to charge the customer in the meantime
            synchronized (callback.getCustomer().getCreditCard()) {
                //if customer does not have enough money
                if (callback.getCustomer().getAvailableCreditAmount() < checkAvailabilityResult.get()) {
                    complete(callback, null);
                    return;
                }
                //take book from inventory
                Future<OrderResult> TakeBookResult = sendEvent(new TakeBookEvent(callback.getOrderSchedule().getBookTitle()));
                if (TakeBookResult == null || TakeBookResult.get() == null || OrderResult.SUCCESSFULLY_TAKEN != TakeBookResult.get()) {
                    complete(callback, null);
                    return;
                }
                //charge the customer
                moneyRegister.chargeCreditCard(callback.getCustomer(), checkAvailabilityResult.get());
            }
            //create receipt
            OrderReceipt orderReceipt = new OrderReceipt(orderId, getName(), callback.getCustomer().getId(),
                    callback.getOrderSchedule().getBookTitle(), checkAvailabilityResult.get(), currentTick,
                    callback.getOrderSchedule().getTick(), processTick);
            orderId++;
            //complete the event
            complete(callback, orderReceipt);
            //file the receipt
            moneyRegister.file(orderReceipt);
            
            //deliver the book
            sendEvent(new DeliveryEvent(callback.getCustomer()));
        });
        latch.countDown();
    }
}
