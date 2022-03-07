package vigenerecrypting;

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

    // find the substring of text starting at startInd and selecting the startIndex + n * jump characters (n > 0)
    static String getStringOnGroup(String text, int startInd, int jump) {
        StringBuilder newTextBuilder = new StringBuilder();
        for (int i = startInd; i < text.length(); i += jump)
            newTextBuilder.append(text.charAt(i));
        return newTextBuilder.toString();
    }

    // calculate the frequency of the symbol = symbolIndex + 'A' in text
    static int calcFreq(int symbolIndex, String text) {
        int freq = 0;
        for (int i = 0; i < text.length(); ++i)
            if (text.charAt(i) == (char) (symbolIndex + 'A'))
                freq++;
        return freq;
    }

    // calculate the coincidence index of a text
    static double calcCoincidenceIndex(String text) {
        double coincidenceIndex = 0;
        for (int i = 0; i < EnglishConstants.NO_LETTERS; ++i) {
            int freq = calcFreq(i, text);
            coincidenceIndex += ((double) (freq * (freq - 1))) /
                    (text.length() * (text.length() - 1));
        }
        return coincidenceIndex;
    }

    // coincidence index approach: find the key length based on the "closest" CI to 0.065 (CI for English)
    public int findKeyLength() {
        int tempLen;
        // find the average CI (the average of the C1 for all textGroups starting at startInd with jump tempLen)
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

        // find the closest CI to 0.065
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

    // more exhaustive method approach: find the key length based on the repetition of trigrams
    public Map<Integer, Integer> findKeyLengthTrigrams() {
        Map<Integer, Integer> bestKeyLenHits = new HashMap<>();

        Map <Integer, Integer> keyLenToHits = new HashMap<>();
        for (int i = 1; i <= MAX_KEY_RETRIES; ++i)
            keyLenToHits.put(i, 0);

        // calculate hits for keys between [2, 20] (a hit is a divisor of the spacing between two identical trigrams)
        for (int i = 0; i + 3 < encryptedText.length(); ++i) {
            String currTrigram = encryptedText.substring(i, i + 3);
            for (int j = i + 3; j + 3 < encryptedText.length(); ++j) {
                String textTrigram = encryptedText.substring(j, j + 3);
                if (currTrigram.equals(textTrigram)) {
                    int spacing = j - i;
                    for (int div = 2; div <= 20; ++div)
                        if (spacing % div == 0)
                            keyLenToHits.put(div, keyLenToHits.get(div) + 1);
                }
            }
        }

        // find the bestKeyLengths for the maxHits (since we will attempt multiple keys)
        int maxHits = Integer.MIN_VALUE;
        int candidateKeyLen = 0;
        for (int tempKeyLen = 2; tempKeyLen <= 20; ++tempKeyLen)
            if (keyLenToHits.get(tempKeyLen) > maxHits) {
                maxHits = keyLenToHits.get(tempKeyLen);
                candidateKeyLen = tempKeyLen;
            }

        bestKeyLenHits.put(candidateKeyLen, maxHits);
        double avgError = 0;
        for (int tempKeyLen = 2; tempKeyLen <= 20; ++tempKeyLen)
            avgError += keyLenToHits.get(tempKeyLen);
        avgError = avgError / 19; // 19 values for the keys to try

        for (int tempKeyLen = 2; tempKeyLen <= 20; ++tempKeyLen)
            if (keyLenToHits.get(tempKeyLen) >= avgError)
                bestKeyLenHits.put(tempKeyLen, keyLenToHits.get(tempKeyLen));
        return bestKeyLenHits;
    }

    // using the keys found for the trigrams, we check each one of them to see which fits better, as we calculate the MCI of a normal text and our shifted text
    public void findKeyTrigrams() {
        Map<Integer, Integer> bestKeyLenHits = new HashMap<>();

        bestKeyLenHits = findKeyLengthTrigrams();
        double bestCumulatedMCI = 0;
        String bestFitKey = "";

        // attempt each found key
        for (Integer tempKeyLength : bestKeyLenHits.keySet()) {
            String tempKey;
            double cumulatedMCI = 0;
            StringBuilder keyBuilder = new StringBuilder();

            // get text groups
            for (int startInd = 0; startInd < tempKeyLength; ++startInd) {
                String textGroup = getStringOnGroup(encryptedText, startInd, tempKeyLength);

                // attempt each of the 26 possible shifts
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

                // find the best shift
                double minMCIDiff = Double.MAX_VALUE, bestMCIfound = 0;
                int bestShift = 0;
                for (Integer shift : shiftedTextsMCI.keySet())
                    if (Math.abs(shiftedTextsMCI.get(shift) - MUTUAL_INDEX_OF_COINCIDENCE) < minMCIDiff) {
                        minMCIDiff = Math.abs(shiftedTextsMCI.get(shift) - MUTUAL_INDEX_OF_COINCIDENCE);
                        bestMCIfound = shiftedTextsMCI.get(shift);
                        bestShift = shift;
                    }

                char toAppend = (char) ((26 - bestShift) % 26 + 'A');
                keyBuilder.append(toAppend);
                cumulatedMCI += bestMCIfound;
            }

            // using the cumulated mci for the key, find the best fir key
            tempKey = keyBuilder.toString();
            cumulatedMCI = cumulatedMCI / tempKeyLength;
            if (cumulatedMCI > bestCumulatedMCI) {
                bestCumulatedMCI = cumulatedMCI;
                bestFitKey = tempKey;
            }
        }

        key = bestFitKey;
        // key = shortenKey(key);
    }

    // calculate the mutual coincidence index between a normal text and a given text
    static public double calcMutualCoincidenceIndex(String shiftedString) {
        double mutualCI = 0;
        for (int i = 0; i < EnglishConstants.NO_LETTERS; ++i) {
            double normalTextProb = findNormalTextProb(shiftedString.length(), (char) (i + 'A'));
            int shiftedFreq = calcFreq(i, shiftedString);
            mutualCI += (normalTextProb * (((double) shiftedFreq) / shiftedString.length()));
        }
        return mutualCI;
    }

    // get the normal text probability for a certain letter
    static public double findNormalTextProb(int textLength, char letter) {
        double currSymbolProb = EnglishConstants.LETTER_FREQUENCIES.get(letter) / 100;
        return currSymbolProb;
    }

    // sometimes we can get repeats like KEYKEY, while the key is actually KEY, that's why we might want to shorten it
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

    // find the key using the keyLength found with the IC approach
    public void findKey() {
        keyLength = findKeyLength();
        StringBuilder keyBuilder = new StringBuilder();

        // get the text groups
        for (int startInd = 0; startInd < keyLength; ++startInd) {
            String textGroup = getStringOnGroup(encryptedText, startInd, keyLength);

            // attempt each of the 26 possible shifts
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

            // find the best shift
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

    // decrypt the text using the formula: plainText[i] = (encryptedText[i] - 'A' - (key[i mod keyLength] - 'A')) % 26 + 'A'
    public void decryptText() {
        if (RunSettings.decryptSet == 1) {
            findKeyTrigrams();
        }
        else {
            findKey();
        }

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
