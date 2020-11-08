package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static class ResourcesInstanceHolder{
        private static ResourcesHolder instance = new ResourcesHolder();
    }
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> futuresDeliveryVehicles;
    private ConcurrentLinkedQueue<DeliveryVehicle> deliveryVehicles;

    private ResourcesHolder() {
        futuresDeliveryVehicles = new ConcurrentLinkedQueue<>();
        deliveryVehicles = new ConcurrentLinkedQueue<>();
    }

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourcesInstanceHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
        Future<DeliveryVehicle> future = new Future<>();

        if (!deliveryVehicles.isEmpty()) {
            future.resolve(deliveryVehicles.poll());
        } else {
            futuresDeliveryVehicles.add(future);
        }

        return future;
    }
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
	    if (!futuresDeliveryVehicles.isEmpty()) {
            futuresDeliveryVehicles.poll().resolve(vehicle);
        } else {
            deliveryVehicles.add(vehicle);
        }
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
        for (DeliveryVehicle vehicle: vehicles) {
            releaseVehicle(vehicle);
        }
    }
}
