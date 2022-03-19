package substitutioncrypting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class SubstitutionEncrypter {
    private String plainText;
    private String encryptedText;
    private Map<Character, Character> substitutionTable;

    public  SubstitutionEncrypter() {
    }

    public SubstitutionEncrypter(File textFile, int option) {
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

        substitutionTable = new HashMap<>();
        if (option == 1) {
            generateSubstitutionTable();
        }
        else {
            setDefaultSubstitutionTable();
        }
    }

    private void setDefaultSubstitutionTable() {
        File defaultFile = new File(RunSettings.DEFAULT_SUBSTITUTION_FILE_PATH);
        try {
            Scanner fileReader = new Scanner(defaultFile);
            while (fileReader.hasNextLine()) {
                String lineData = fileReader.nextLine();
                if (lineData.length() >= 3) {
                    Character ch1 = Character.toUpperCase(lineData.charAt(0));
                    Character ch2 = Character.toUpperCase(lineData.charAt(2));
                    if (!((ch1 >= 'A' && ch1 <= 'Z') || (ch2 >= 'A' && ch2 <= 'Z')))
                        throw new IllegalArgumentException("'" + ch1 + "' or '" + ch2 + "' is not a character!");
                    else {
                        substitutionTable.put(ch1, ch2);
                    }
                }
            }
            fileReader.close();
        }
        catch (IOException | IllegalArgumentException err) {
            err.printStackTrace();
        }
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

    // if the option "gen" is set for encryption, we generate a random substitution table
    public void generateSubstitutionTable() {
        Random random = new Random();
        for (int i = 0; i < EnglishConstants.NO_LETTERS; ++i) {
            int si = random.nextInt(0, 25);
            Character ch1 = (char) (i + 'A');
            Character ch2 = (char) (si + 'A');

            if (substitutionTable.values().contains(ch2))
                i--;
            else {
                substitutionTable.put(ch1, ch2);
            }
        }
    }

    // using the substitution table we encrypt the text
    public void encryptPlainText() {
        StringBuilder encryptedTextBuilder = new StringBuilder();
        for (int i = 0; i < plainText.length(); ++i) {
            encryptedTextBuilder.append(substitutionTable.get(plainText.charAt(i)));
        }
        encryptedText = encryptedTextBuilder.toString();
    }

    public void printSubstitutionTable() {
        substitutionTable.entrySet().stream()
                .forEach(pair -> System.out.println(pair.getKey() + ":" + pair.getValue()));
    }

    public void printEncryptedTextToFile(File newTextFile) {
        printSubstitutionTable();
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

    public void printEncryptedTextAndSubstitutionTable() {
        printSubstitutionTable();
        System.out.println(encryptedText);
    }
}
