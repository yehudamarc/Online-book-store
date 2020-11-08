package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.helpers.SerializationUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
    private static class MoneyRegisterInstanceHolder{
        private static MoneyRegister instance = new MoneyRegister();
    }

	private AtomicInteger totalEarnings;
    private List<OrderReceipt> receipts;

    private MoneyRegister() {
        receipts = new LinkedList<>();
        totalEarnings = new AtomicInteger(0);
    }

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegisterInstanceHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public synchronized void file (OrderReceipt r) {
        receipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
        return totalEarnings.get();
    }

    /**
     * Using atomic instruction to set the total earnings of the store
     */
    private void setTotalEarnings(int bookPrice) {
        int val;
        do {
            val = totalEarnings.get();
        } while (!totalEarnings.compareAndSet(val, val + bookPrice));
    }
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
        c.chargeCreditCard(amount);
        setTotalEarnings(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
        SerializationUtils.serializeObject(receipts, filename);
	}
}
