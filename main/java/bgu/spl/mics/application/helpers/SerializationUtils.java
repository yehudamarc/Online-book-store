package bgu.spl.mics.application.helpers;

import java.io.*;

public class SerializationUtils {

    /**
     * Serializing a given object to a given file path
     *
     * @param o        The object to serialize
     * @param filePath The file to serialize the given object to
     */
    public static void serializeObject(Object o, String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(o);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Deserialize a given object from a given file path
     *
     * @param filePath The object to deserialize from this file
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(String filePath) {
        T object;
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = (T) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }
}
