package bgu.spl.mics;

import bgu.spl.mics.application.helpers.SerializationUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SerializationUtilsTest {
    private static String value;
    private static String serializeObject;
    private static String filePath;

    @BeforeClass
    public static void beforeClass() {
        value = "Done!";
        serializeObject = value;
        filePath = "src/test/java/bgu/spl/mics/output/string.ser";
    }

    @Before
    public void before() {
    }

    /**
     * Testing serialize
     */
    @Test
    public void serializeObjectTest() {
        SerializationUtils.serializeObject(serializeObject, filePath);
        assertEquals(true, (new File(filePath)).exists());
    }

    /**
     * Testing deserialize
     */
    @Test
    public void serializeDeserializeTest() {
        SerializationUtils.serializeObject(serializeObject, filePath);
        String s = SerializationUtils.deserializeObject(filePath);
        assertEquals(value, s);
    }

    @After
    public void after() {
        FileUtils.deleteQuietly(new File(filePath));
    }
}
