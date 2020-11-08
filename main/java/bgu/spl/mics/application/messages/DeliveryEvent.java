package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event<Boolean> {

    private String address;
    private int distance;

    public DeliveryEvent(Customer customer){
        address = customer.getAddress();
        distance = customer.getDistance();
    }

    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }
}
