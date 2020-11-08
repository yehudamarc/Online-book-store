package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {

	private Inventory inventory;
	private CountDownLatch latch;

	public InventoryService(String name, CountDownLatch latch) {
		super(name);
		this.latch = latch;
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, callback -> terminate());
		subscribeEvent(CheckAvailability.class, callback -> {
			String bookTitle = callback.getBookTitle();
			int output = inventory.checkAvailabiltyAndGetPrice(bookTitle);
			complete(callback, output);
		});
		subscribeEvent(TakeBookEvent.class, callback -> {
			OrderResult output = inventory.take(callback.getBookTitle());
			complete(callback, output);
		});
		latch.countDown();
	}
}
