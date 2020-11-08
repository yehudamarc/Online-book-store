package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch latch;

	public LogisticsService(String name, CountDownLatch latch) {
		super(name);
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, callback -> terminate());
		subscribeEvent(DeliveryEvent.class, callback -> {
			Future<Future<DeliveryVehicle>> acquireVehicleResult = sendEvent(new AcquireVehicleEvent());
			//check if acquired vehicle successfully
			if(acquireVehicleResult != null && acquireVehicleResult.get() != null){
                DeliveryVehicle myVehicle = acquireVehicleResult.get().get();
				if (myVehicle != null) {
                    myVehicle.deliver(callback.getAddress(), callback.getDistance());
                    sendEvent(new ReleaseVehicleEvent(myVehicle));
                }
			}
		});
		latch.countDown();
	}
}

