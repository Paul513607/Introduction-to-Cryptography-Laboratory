package attacks;

import descryptosystem.DESCryptosystem;
import lombok.Data;
import util.BitStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class MeetInTheMiddle2DESAttack {
    private List<String> key1Values = new ArrayList<>();
    private List<String> textsConvertedWithKey1 = new ArrayList<>();
    private String knownPlainText;
    private String knownEncryptedText;

    private List<String> possibleKey1 = new ArrayList<>();
    private List<String> possibleKey2 = new ArrayList<>();

    public MeetInTheMiddle2DESAttack() {
    }

    private void findTextsWithKey1() {
        for (char ch = 0; ch < 256; ++ch) {
            String chAsStr = "" + ch;
            String key = chAsStr.repeat(8);
            key1Values.add(key);
            String keyBitString = BitStringConverter.convertMsgToBitString(key);

            DESCryptosystem desCryptosystem = new DESCryptosystem();
            desCryptosystem.generateSubKeys(keyBitString);
            desCryptosystem.encryptText(knownPlainText);

            textsConvertedWithKey1.add(desCryptosystem.getEncryptedText());
        }
    }

    public void meetInTheMiddleAttack() {
        findTextsWithKey1();
        for (char ch = 0; ch < 256; ++ch) {
            String chAsStr = "" + ch;
            String key = chAsStr.repeat(8);
            String keyBitString = BitStringConverter.convertMsgToBitString(key);

            DESCryptosystem desCryptosystem = new DESCryptosystem();
            desCryptosystem.generateSubKeys(keyBitString);
            desCryptosystem.decryptText(knownEncryptedText);

            String currDecryptedText = desCryptosystem.getPlainText();
            for (int index = 0; index < textsConvertedWithKey1.size(); ++index)
                if (textsConvertedWithKey1.get(index).equals(currDecryptedText)) {
                    possibleKey1.add(key1Values.get(index));
                    possibleKey2.add(key);
                }
        }
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
            knownPlainText = text;
        else if (option == 1)
            knownEncryptedText = text;
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