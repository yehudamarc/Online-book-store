package bgu.spl.mics.application.passiveObjects;

public class Services {
    private Time time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;

    public Services(Time time, int selling, int inventoryService, int logistics, int resourcesService,
                    Customer[] customers) {
        this.time = time;
        this.selling = selling;
        this.inventoryService = inventoryService;
        this.logistics = logistics;
        this.resourcesService = resourcesService;
        this.customers = customers;
    }

    public Customer[] getCustomers() {
        return customers;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public int getSelling() {
        return selling;
    }

    public Time getTime() {
        return time;
    }
}
