package descryptosystem;

import lombok.Data;
import util.BitStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
public class DoubleDESCryptosystem {
    private String key1 = BitStringConverter.convertMsgToBitString("CCCCCCCC");
    private String key2 = BitStringConverter.convertMsgToBitString("}}}}}}}}");
    private DESCryptosystem desCryptosystem = new DESCryptosystem();
    private String plainText;
    private String encryptedText;

    public DoubleDESCryptosystem() throws IllegalArgumentException {
        String stringKey1 = BitStringConverter.convertBitStringToMsg(key1);
        String stringKey2 = BitStringConverter.convertBitStringToMsg(key2);

        if (stringKey1.length() != 8)
            throw new IllegalArgumentException("Error for key1. The test keys sizes must be 8 bytes (64 bits).");
        if (stringKey2.length() != 8)
            throw new IllegalArgumentException("Error for key2. The test keys sizes must be 8 bytes (64 bits).");

        if (stringKey1.replaceAll(Character.toString(stringKey1.charAt(0)), "").length() != 0)
            throw new IllegalArgumentException("Error for key1. The test keys must be of form \"b^8\", where \"b\" is a byte.");
        if (stringKey2.replaceAll(Character.toString(stringKey2.charAt(0)), "").length() != 0)
            throw new IllegalArgumentException("Error for key2. The test keys must be of form \"b^8\", where \"b\" is a byte.");
    }

    public String getKey1Formatted() {
        return BitStringConverter.convertBitStringToMsg(key1);
    }

    public String getKey2Formatted() {
        return BitStringConverter.convertBitStringToMsg(key2);
    }

    public void encryptText(String inputText) {
        plainText = inputText;

        desCryptosystem.generateSubKeys(key1);
        desCryptosystem.encryptText(plainText);
        String tempText = desCryptosystem.getEncryptedText();

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);
        desCryptosystem.getGeneratedSubKeys().clear();

        desCryptosystem.generateSubKeys(key2);
        desCryptosystem.encryptText(tempText);
        encryptedText = desCryptosystem.getEncryptedText();

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);
        desCryptosystem.getGeneratedSubKeys().clear();
    }

    public void decryptText(String inputText) {
        encryptedText = inputText;

        desCryptosystem.generateSubKeys(key2);
        desCryptosystem.decryptText(encryptedText);
        String tempText = desCryptosystem.getPlainText();

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);
        desCryptosystem.getGeneratedSubKeys().clear();

        desCryptosystem.generateSubKeys(key1);
        desCryptosystem.decryptText(tempText);
        plainText = desCryptosystem.getPlainText();

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);
        desCryptosystem.getGeneratedSubKeys().clear();
    }

    public void readTextFromFile(String path, int option) {
        File file = new File(path);

        String text = "";
        try {
            text = Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (option == 0)
            plainText = text;
        else if (option == 1)
            encryptedText = text;
    }

    public static void writeTextToFile(String path, String text) {
        File file = new File(path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
