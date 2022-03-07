package vigenerecrypting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class VigenereEncrypter {
    private String key;
    private String plainText;
    private String encryptedText;

    public  VigenereEncrypter() {
    }

    public VigenereEncrypter(File textFile, int option) {
        try {
            Scanner myReader = new Scanner(textFile);
            StringBuilder stringBuilder = new StringBuilder();
            while (myReader.hasNextLine()) {
                String lineData = myReader.nextLine();
                stringBuilder.append(lineData);
            }
            String text = stringBuilder.toString();
            convertToPlainText(text);
            myReader.close();
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        if (option == 1) {
            generateKey();
        }
        else if (option == 2) {
            key = RunSettings.keyFromArgs;
            key.toUpperCase();
        }
        else {
            key = "AAAAAAAAB";
            key.toUpperCase();
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlainText() {
        return plainText;
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    // removes all non-letter characters and converts all the others to uppercase
    public void convertToPlainText(String text) {
        StringBuilder plainTextBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            Character ch = text.charAt(i);
            if (ch >= 'a' && ch <= 'z')
                ch = Character.toUpperCase(ch);
            if (ch >= 'A' && ch <= 'Z')
                plainTextBuilder.append(ch);
        }
        plainText = plainTextBuilder.toString();
    }

    // if the option "gen" is set for encryption, we generate a random key
    public void generateKey() {
        Random random = new Random();
        int keyLength = random.nextInt(2, 21); // generate a key of length [2, 20]
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < keyLength; ++i) {
            char ch = (char) random.nextInt('A', 'Z' + 1);
            keyBuilder.append(ch);
        }
        key = keyBuilder.toString();
    }

    // using the key we encrypt the text (formula: encrypted[i] = (plainText[i] - 'A' + (key[i mod keyLength] - 'A')) % 26 + 'A')
    public void encryptPlainText() {
        int keyLength = key.length();
        StringBuilder encryptedTextBuilder = new StringBuilder();
        for (int i = 0; i < plainText.length(); ++i) {
            int valPlainText = plainText.charAt(i) - 'A';
            int valKey = key.charAt(i % keyLength) - 'A';
            char charEncryptedText = (char) ((valPlainText + valKey) % EnglishConstants.NO_LETTERS + 'A');
            encryptedTextBuilder.append(charEncryptedText);
        }
        encryptedText = encryptedTextBuilder.toString();
    }

    public void printEncryptedTextToFile(File newTextFile) {
        System.out.println("Key: " + key);
        try {
            if (newTextFile.createNewFile())
                System.out.println("Created file: " + newTextFile.getPath());
            else
                System.out.println("File: " + newTextFile.getPath() + " already exists. Will overwrite it.");
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(newTextFile.getPath());
            fileWriter.write(encryptedText);
            fileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void printEncryptedText() {
        System.out.println(encryptedText);
    }
}
