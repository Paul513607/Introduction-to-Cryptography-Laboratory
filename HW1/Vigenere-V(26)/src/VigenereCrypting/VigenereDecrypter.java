package VigenereCrypting;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class VigenereDecrypter {
    static final private double INDEX_OF_COINCIDENCE = 0.065;
    static final private double MUTUAL_INDEX_OF_COINCIDENCE = 0.065;
    static final private int MAX_KEY_RETRIES = 20;

    private String encryptedText;
    private String plainText;
    int keyLength;
    private String key;

    public VigenereDecrypter() {
    }

    public VigenereDecrypter(File encryptedFile) {
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

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getPlainText() {
        return plainText;
    }

    public String getKey() {
        return key;
    }

    static String getStringOnGroup(String text, int startInd, int jump) {
        StringBuilder newTextBuilder = new StringBuilder();
        for (int i = startInd; i < text.length(); i += jump)
            newTextBuilder.append(text.charAt(i));
        return newTextBuilder.toString();
    }

    static int calcFreq(int symbolIndex, String text) {
        int freq = 0;
        for (int i = 0; i < text.length(); ++i)
            if (text.charAt(i) == (char) (symbolIndex + 'A'))
                freq++;
        // System.out.println((char) (symbolIndex + 'A') + ": " + freq);
        return freq;
    }

    static double calcCoincidenceIndex(String text) {
        double coincidenceIndex = 0;
        for (int i = 0; i < EnglishConstants.NO_LETTERS; ++i) {
            int freq = calcFreq(i, text);
            coincidenceIndex += ((double) (freq * (freq - 1))) /
                    (text.length() * (text.length() - 1));
        }
        return coincidenceIndex;
    }

    public int findKeyLength() {
        int tempLen;
        Map<Integer, Double> keyLenToCI = new HashMap<>();
        for (tempLen = 1; tempLen <= MAX_KEY_RETRIES; ++tempLen) {
            ArrayList<Double> coincidenceIndexList = new ArrayList<>();
            for (int startInd = 0; startInd < tempLen; ++startInd) {
                String textGroup = getStringOnGroup(encryptedText, startInd, tempLen);
                double coincidenceIndex = calcCoincidenceIndex(textGroup);
                coincidenceIndexList.add(coincidenceIndex);
            }

            double avgCI = 0;
            for (Double ci : coincidenceIndexList)
                avgCI += ci;
            avgCI = avgCI / coincidenceIndexList.size();

            keyLenToCI.put(tempLen, avgCI);
        }

        double minDiff = Double.MAX_VALUE;
        int bestKeyLen = 0;
        for (Integer keyLength : keyLenToCI.keySet()) {
            double currAvgValue = keyLenToCI.get(keyLength);
            if (Math.abs(currAvgValue - INDEX_OF_COINCIDENCE) < minDiff) {
                bestKeyLen = keyLength;
                minDiff = Math.abs(currAvgValue - INDEX_OF_COINCIDENCE);
            }
        }

        int newBestKeyLen = bestKeyLen;

        // If we find a key of too big length, we attempt to find a smaller best key in its divisors
        if (newBestKeyLen >= 10) {
            minDiff = Double.MAX_VALUE;
            for (int newKeyLen = 1; newKeyLen < bestKeyLen; ++newKeyLen)
                if (bestKeyLen % newKeyLen == 0) {
                    double currAvgValue = keyLenToCI.get(newKeyLen);
                    if (Math.abs(currAvgValue - INDEX_OF_COINCIDENCE) < minDiff) {
                        newBestKeyLen = newKeyLen;
                        minDiff = Math.abs(currAvgValue - INDEX_OF_COINCIDENCE);
                    }
                }
        }
        return newBestKeyLen;
    }

    static public double calcMutualCoincidenceIndex(String shiftedString) {
        double mutualCI = 0;
        for (int i = 0; i < EnglishConstants.NO_LETTERS; ++i) {
            double normalTextProb = findNormalTextProb(shiftedString.length(), (char) (i + 'A'));
            int shiftedFreq = calcFreq(i, shiftedString);
            mutualCI += (normalTextProb * (((double) shiftedFreq) / shiftedString.length()));
        }
        return mutualCI;
    }

    static public double findNormalTextProb(int textLength, char letter) {
        double currSymbolProb = EnglishConstants.LETTER_FREQUENCIES.get(letter) / 100;
        return currSymbolProb;
    }

    static public String shortenKey(String key) {
        int maxLength = Integer.MIN_VALUE;
        String candidateKey = key;
        for (int i = 1; i < key.length() / 2 + 1; ++i) {
            String test = key.substring(0, i);
            boolean ok = true;
            for (int j = i; j + i <= key.length() && ok; j += i) {
                if (!test.equals(key.substring(j, j + i))) {
                    ok = false;
                }
            }

            if (ok) {
                maxLength = test.length();
            }
        }
        if (maxLength > 0)
            candidateKey = key.substring(0, maxLength);
        return candidateKey;
    }

    public void findKey() {
        keyLength = findKeyLength();
        StringBuilder keyBuilder = new StringBuilder();

        for (int startInd = 0; startInd < keyLength; ++startInd) {
            String textGroup = getStringOnGroup(encryptedText, startInd, keyLength);

            Map<Integer, Double> shiftedTextsMCI = new HashMap<>();
            for (int shift = 0; shift < EnglishConstants.NO_LETTERS; ++shift) {
                StringBuilder shiftedTextGroupBuilder = new StringBuilder();

                for (int i = 0; i < textGroup.length(); ++i) {
                    int valEncryptedText = textGroup.charAt(i) - 'A';
                    int currVal = ((valEncryptedText + shift) % EnglishConstants.NO_LETTERS);

                    shiftedTextGroupBuilder.append((char) (currVal + 'A'));
                }

                double tempMCI = calcMutualCoincidenceIndex(shiftedTextGroupBuilder.toString());
                shiftedTextsMCI.put(shift, tempMCI);
            }

            double minMCIDiff = Double.MAX_VALUE;
            int bestShift = 0;
            for (Integer shift : shiftedTextsMCI.keySet())
                if (Math.abs(shiftedTextsMCI.get(shift) - MUTUAL_INDEX_OF_COINCIDENCE) < minMCIDiff) {
                    minMCIDiff = Math.abs(shiftedTextsMCI.get(shift) - MUTUAL_INDEX_OF_COINCIDENCE);
                    bestShift = shift;
                }

            char toAppend = (char) ((26 - bestShift) % 26 + 'A');
            keyBuilder.append(toAppend);
        }

        key = keyBuilder.toString();
        // key = shortenKey(key);
    }

    public void decryptText() {
        findKey();

        int keyLength = key.length();
        StringBuilder plainTextBuilder = new StringBuilder();
        for (int i = 0; i < encryptedText.length(); ++i) {
            int valEncryptedText = encryptedText.charAt(i) - 'A';
            int valKey = key.charAt(i % keyLength) - 'A';
            int valPlainText = ((valEncryptedText - valKey) % EnglishConstants.NO_LETTERS) >= 0 ?
                    ((valEncryptedText - valKey) % EnglishConstants.NO_LETTERS) : ((valEncryptedText - valKey) % EnglishConstants.NO_LETTERS) + EnglishConstants.NO_LETTERS;
            char charPlainText = (char) (valPlainText + 'A');
            plainTextBuilder.append(charPlainText);
        }
        plainText = plainTextBuilder.toString();
    }

    public void printKey() {
        System.out.println("Key: " + key + " (length: " + key.length() + ")");
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

    public void printDecryptedTextAndKey() {
        System.out.println("Key: " + key + " (length: " + key.length() + ")");
        System.out.println(plainText);
    }
}
