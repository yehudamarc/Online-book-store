package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String name;
	private int id;
	private String address;
	private int distance;
	private CreditCard creditCard;
	private List<OrderReceipt> receiptList;
	private Hashtable<Integer, LinkedList<OrderSchedule>> tickToOrderScheduled;
	private OrderSchedule[] orderSchedule;

	public Customer(String name, int id, String address, int distance, CreditCard creditCard, OrderSchedule[] orderSchedules) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.distance = distance;
        this.creditCard = creditCard;
        this.receiptList = new LinkedList<>();
        this.orderSchedule = orderSchedules;
    }

    // TODO: make gson call the object constructor
    public void mapTickToOrderSchedule() {
		this.receiptList = new LinkedList<>();
		this.tickToOrderScheduled = new Hashtable<>();
		for (OrderSchedule orderSchedule : this.orderSchedule) {
			if (!this.tickToOrderScheduled.containsKey(orderSchedule.getTick())) {
				this.tickToOrderScheduled.put(orderSchedule.getTick(), new LinkedList<>());
			}
			this.tickToOrderScheduled.get(orderSchedule.getTick()).add(orderSchedule);
		}
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receiptList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.creditCard.getAmount();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.creditCard.getNumber();
	}

	public void addReceipt(OrderReceipt receipt){
		receiptList.add(receipt);
	}

	public void chargeCreditCard(int bill) {
		this.creditCard.setAmount(this.creditCard.getAmount() - bill);
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

    public LinkedList<OrderSchedule> getOrderSchedulesByTick(int tick) {
		if (this.tickToOrderScheduled.containsKey(tick)) {
			return this.tickToOrderScheduled.get(tick);
		}
        return new LinkedList<>();
    }
}

