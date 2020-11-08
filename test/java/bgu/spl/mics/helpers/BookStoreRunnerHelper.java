package bgu.spl.mics.helpers;

import bgu.spl.mics.BookStoreRunnerTest;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BookStoreRunnerHelper {

    public static List<FilePair> getInputJsonFilesList() throws IOException {
        List<FilePair> data = new LinkedList<>();
        for (int i = 2; i <= 13; i++) {
            FilePair filePair = new FilePair((new File("src/test/java/bgu/spl/mics/input/" + BookStoreRunnerTest.class.getSimpleName() +
                    "/test_" + i + "/" + "/test_" + i + ".json")).getCanonicalPath(), (new File("src/test/java/bgu/spl/mics/output/" + BookStoreRunnerTest.class.getSimpleName() +
                    "/test_" + i + "/" + "/test_" + i + ".json")).getCanonicalPath());
            data.add(filePair);
        }

        return data;
    }
}
