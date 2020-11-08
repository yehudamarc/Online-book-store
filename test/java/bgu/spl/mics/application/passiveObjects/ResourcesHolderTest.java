package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.helpers.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ResourcesHolderTest {
    private static ResourcesHolder resourcesHolder;
    private static DeliveryVehicle[] vehicles;


    @BeforeClass
    public static void beforeClass() {
        resourcesHolder = ResourcesHolder.getInstance();

        String vehiclesConfigFilePath = "src/test/java/bgu/spl/mics/input/vehicles.json";
        try {
            vehicles = JsonUtils.deserializeJsonToObj(FileUtils.readFileToString(new File(vehiclesConfigFilePath),
                    Charset.defaultCharset()), DeliveryVehicle[].class);
        } catch (IOException e) {
            fail(e.toString());
        }
    }

    @Before
    public void before() {
        // Before each test loading vehicles
        resourcesHolder.load(vehicles);
    }

    /**
     * It should get a vehicle immediately as enough vehicles have loaded before
     */
    @Test
    public void verifyAcquireVehicle() {
        Future<DeliveryVehicle> future = resourcesHolder.acquireVehicle();
        assertEquals(vehicles[0].getLicense(), future.get().getLicense());
    }

    /**
     * It should wait for a new vehicle to be released
     */
    @Test
    public void verifyReleaseVehicle() {
        Future<DeliveryVehicle> future;

        // Emptying the vehicles from the resource holder, getting the last future as unresolved
        do {
            future = resourcesHolder.acquireVehicle();
        }
        while (future.isDone());

        resourcesHolder.releaseVehicle(vehicles[0]);
        assertEquals(vehicles[0].getLicense(), future.get().getLicense());
    }

    @After
    public void after() {
    }
}
