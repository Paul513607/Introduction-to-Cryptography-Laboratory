package utilities;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

public class Compressor {
    private final String DEFAULT_REPEAT_STRING = "01";
    private final int MAX_BITSET_LEN = RunSettings.MAX_ITERATIONS;
    private final String defaultPath;
    private final String toCompress;

    public Compressor(String toCompress, String defaultPath) {
        this.toCompress = toCompress;
        this.defaultPath = defaultPath;
    }

    public void compressDefault() throws IOException {
        StringBuilder defaultString = new StringBuilder();
        for (int i = 0; i < MAX_BITSET_LEN / DEFAULT_REPEAT_STRING.length(); ++i) {
            defaultString.append(DEFAULT_REPEAT_STRING);
        }

        File defaultFile = new File(defaultPath + "defaultTest.txt");
        try {
            if (defaultFile.createNewFile())
                System.out.println("Created file: " + defaultFile.getPath());
            else
                System.out.println("File: " + defaultFile.getPath() + " already exists. Will overwrite it.");
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileWriter defaultFileWriter = new FileWriter(defaultFile);
            defaultFileWriter.write(defaultString.toString());
            defaultFileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        FileInputStream fis = new FileInputStream(defaultPath + "defaultTest.txt");
        FileOutputStream fos = new FileOutputStream(defaultPath + "defaultTest.zip");
        DeflaterOutputStream dos = new DeflaterOutputStream(fos);

        int data;
        while ((data = fis.read()) != -1) {
            dos.write(data);
        }

        //close the file
        fis.close();
        dos.close();
    }

    public void compressInputString() throws IOException {
        File defaultFile = new File(defaultPath + "test.txt");
        try {
            if (defaultFile.createNewFile())
                System.out.println("Created file: " + defaultFile.getPath());
            else
                System.out.println("File: " + defaultFile.getPath() + " already exists. Will overwrite it.");
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileWriter defaultFileWriter = new FileWriter(defaultFile);
            defaultFileWriter.write(toCompress);
            defaultFileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        FileInputStream fis = new FileInputStream(defaultPath + "test.txt");
        FileOutputStream fos = new FileOutputStream(defaultPath + "test.zip");
        DeflaterOutputStream dos = new DeflaterOutputStream(fos);

        int data;
        while ((data = fis.read()) != -1) {
            dos.write(data);
        }

        //close the file
        fis.close();
        dos.close();
    }
}
