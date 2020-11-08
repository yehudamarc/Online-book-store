package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private CountDownLatch latch;
	private List<Future<DeliveryVehicle>> futureList;

	public ResourceService(String name, CountDownLatch latch) {
		super(name);
		this.latch = latch;
        futureList = new LinkedList<>();
		resourcesHolder = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, callback ->  {
		    for (Future<DeliveryVehicle> future : futureList) {
		        if (!future.isDone()){
                    future.resolve(null);
                }
            }
		    terminate();
        });
		subscribeEvent(AcquireVehicleEvent.class, callback -> {
		    Future<DeliveryVehicle> future = resourcesHolder.acquireVehicle();
		    futureList.add(future);
		    complete(callback, future);
        });
		subscribeEvent(ReleaseVehicleEvent.class, callback -> {
		    resourcesHolder.releaseVehicle(callback.getDeliveryVehicle());
        });
		latch.countDown();
	}
}
