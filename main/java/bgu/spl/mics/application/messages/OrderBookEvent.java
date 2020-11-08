package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

public class OrderBookEvent implements Event<OrderReceipt> {

    private Customer customer;
    private OrderSchedule orderSchedule;

    public OrderBookEvent(Customer customer, OrderSchedule orderSchedule) {
        this.customer = customer;
        this.orderSchedule = orderSchedule;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderSchedule getOrderSchedule() {
        return orderSchedule;
    }
}
