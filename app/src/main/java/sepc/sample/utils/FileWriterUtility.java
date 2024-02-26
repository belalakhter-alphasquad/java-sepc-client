package sepc.sample.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class FileWriterUtility {
    private static FileWriterUtility instance;
    private BufferedWriter writer;

    private FileWriterUtility(String filePath) throws IOException {
        writer = new BufferedWriter(new FileWriter(filePath, true)); // true to append
    }

    public static synchronized FileWriterUtility getInstance(String filePath) throws IOException {
        if (instance == null) {
            instance = new FileWriterUtility(filePath);
        }
        return instance;
    }

    public void writeLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
