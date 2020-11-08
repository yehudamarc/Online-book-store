package bgu.spl.mics.application.passiveObjects;

import java.util.HashMap;

public class BookStoreManager {
    private BookInventoryInfo[] initialInventory;
    // TODO: check
    private Vehicles[] initialResources;
    private Services services;

    public BookStoreManager(BookInventoryInfo[] initialInventory, Vehicles[] initialResources, Services services) {
        this.initialInventory = initialInventory;
        this.initialResources = initialResources;
        this.services = services;
    }

    public Services getServices() {
        return services;
    }

    public Vehicles[] getInitialResources() {
        return initialResources;
    }

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public int getNumberOfServices() {
        return services.getLogistics() + services.getCustomers().length + services.getInventoryService() +
                services.getResourcesService() + services.getSelling() + 1;
    }

    public HashMap<Integer, Customer> getCustomersAsHashMap() {
        HashMap<Integer, Customer> customersHashMap = new HashMap<>();

        for (Customer customer : services.getCustomers()) {
            customersHashMap.put(customer.getId(), customer);
        }

        return customersHashMap;
    }
}
