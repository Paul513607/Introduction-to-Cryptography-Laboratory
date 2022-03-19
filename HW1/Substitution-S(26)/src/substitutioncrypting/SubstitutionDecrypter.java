package substitutioncrypting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SubstitutionDecrypter {
    static public final double CLOSENESS_FACTOR = 0.5;

    private String encryptedText;
    private String plainText;
    private Map<Character, Character> substitutionTable;
    private Map<Character, Double> letterFrequencies;
    private Map<String, Double> bigramFrequencies;
    private Map<String, Double> trigramFrequencies;
    private Map<Character, Set<Character>> letterCloseness;

    public SubstitutionDecrypter() {
    }

    public SubstitutionDecrypter(File encryptedFile) {
        letterFrequencies = new HashMap<>();
        bigramFrequencies = new HashMap<>();
        trigramFrequencies = new HashMap<>();
        letterCloseness = new HashMap<>();

        substitutionTable = new HashMap<>();

        try {
            Scanner myReader = new Scanner(encryptedFile);
            StringBuilder stringBuilder = new StringBuilder();
            while (myReader.hasNextLine()) {
                String lineData = myReader.nextLine();
                stringBuilder.append(lineData);
            }
            encryptedText = stringBuilder.toString();
            myReader.close();
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }
    }

    public void findLetterCloseness () {
        for (Character character : EnglishConstants.LETTER_FREQUENCIES.keySet()) {
            letterCloseness.put(character, new HashSet<>());
            Set<Character> currentSet = new HashSet<>();
            for (Character character1 : EnglishConstants.LETTER_FREQUENCIES.keySet()) {
                if (Math.abs(EnglishConstants.LETTER_FREQUENCIES.get(character) - EnglishConstants.LETTER_FREQUENCIES.get(character1)) <= CLOSENESS_FACTOR)
                    continue;
                else
                    currentSet.add(character1);
            }
            letterCloseness.put(character, currentSet);
        }
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getPlainText() {
        return plainText;
    }

    public Map<Character, Character> getSubstitutionTable() {
        return substitutionTable;
    }

    public void calcFrequencies() {
        for (int i = 0; i < encryptedText.length(); ++i) {
            if (!letterFrequencies.containsKey(encryptedText.charAt(i)))
                letterFrequencies.put(encryptedText.charAt(i), 1.0);
            else
                letterFrequencies.put(encryptedText.charAt(i), letterFrequencies.get(encryptedText.charAt(i)) + 1);
        }

        letterFrequencies.replaceAll((ch, value) -> letterFrequencies.get(ch) / encryptedText.length() * 100.0);

        for (int i = 0; i + 1 < encryptedText.length(); ++i) {
            String currBigram = encryptedText.substring(i, i + 2);
            if (!bigramFrequencies.containsKey(currBigram))
                bigramFrequencies.put(currBigram, 1.0);
            else
                bigramFrequencies.put(currBigram, bigramFrequencies.get(currBigram) + 1);
        }

        bigramFrequencies.replaceAll((bigram, value) -> bigramFrequencies.get(bigram) / Math.ceil(encryptedText.length() / 2.0) * 100.0);

        for (int i = 0; i + 2 < encryptedText.length(); ++i) {
            String currTrigram = encryptedText.substring(i, i + 3);
            if (!trigramFrequencies.containsKey(currTrigram))
                trigramFrequencies.put(currTrigram, 1.0);
            else
                trigramFrequencies.put(currTrigram, trigramFrequencies.get(currTrigram) + 1);
        }

        trigramFrequencies.replaceAll((trigram, value) -> trigramFrequencies.get(trigram) / Math.ceil(encryptedText.length() / 3.0) * 100.0);
    }

    private boolean isCloseTo(double freq1, double freq2) {
        if (Math.abs(freq1 - freq2) <= CLOSENESS_FACTOR)
            return true;
        return false;
    }

    public void findSubstitutionTable() {
        findLetterCloseness();
        calcFrequencies();
        Character mostFreqLetter, secondMostFreqLetter;
        double mostFreq = 0.0, secondMostFreq = 0.0;
        for (Character ch : letterFrequencies.keySet()) {
            //if (letterFrequencies.get(mostFreq) >= 0.0)
                continue;
        }

        System.out.println(letterFrequencies);
        System.out.println(bigramFrequencies);
        System.out.println(trigramFrequencies);
        System.out.println(letterCloseness);
    }

    public void decryptText() {
        findSubstitutionTable();

    }

    public void printSubstitutionTable() {
        substitutionTable.entrySet().stream()
                .forEach(pair -> System.out.println(pair.getKey() + ":" + pair.getValue()));
    }

    public void printDecryptedTextToFile(File newTextFile) {
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
            fileWriter.write(plainText);
            fileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void printDecryptedTextAndSubstitutionTable() {
        printSubstitutionTable();
        System.out.println(plainText);
    }
}
