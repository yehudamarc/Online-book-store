package bgu.spl.mics.application.passiveObjects;

public class Vehicles {
    private DeliveryVehicle[] vehicles;

    public Vehicles(DeliveryVehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    public DeliveryVehicle[] getVehicles() {
        return vehicles;
    }
}
