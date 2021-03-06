package cryptographichashing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.BitStringHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SHA1HasherTest {
    public static final double HAMMING_DIST_MARGIN = 0.4;

    public String plainText;
    public List<List<String>> hexValueWords;
    public List<List<List<String>>> intermediateValues;
    public String resultHash;

    @BeforeEach
    public void setUp() {
        plainText = null;
        hexValueWords = new ArrayList<>();
        intermediateValues = new ArrayList<>();
        resultHash = null;
    }

    @AfterEach
    public void clearTestData() {
        plainText = null;
        hexValueWords = null;
        intermediateValues = null;
        resultHash = null;
    }

    public void readFile1() throws FileNotFoundException {
        File file = new File("src/main/resources/test-files/test-file1.txt");
        Scanner scanner = new Scanner(file);

        scanner.nextLine();
        plainText = scanner.nextLine();
        for (int i = 0; i < 8; ++i)
            scanner.nextLine();

        int blockIndex = Integer.parseInt(scanner.nextLine());
        hexValueWords.add(new ArrayList<>());
        scanner.nextLine();

        for (int i = 0; i < 16; ++i) {
            String line = scanner.nextLine();
            int spaceIndex = line.lastIndexOf(' ');
            String hex = line.substring(spaceIndex + 1);
            hexValueWords.get(blockIndex).add(hex);
        }
        scanner.nextLine();

        intermediateValues.add(new ArrayList<>());
        for (int i = 0; i < 80; ++i) {
            String line = scanner.nextLine();
            int index = line.indexOf(':');
            line = line.substring(index + 2);
            String[] strings = line.split("\\s+");
            intermediateValues.get(blockIndex).add(new ArrayList<>());
            for (String string : strings) {
                intermediateValues.get(blockIndex).get(i).add(string);
            }
        }

        for (int i = 0; i < 8; ++i)
            scanner.nextLine();

        resultHash = scanner.nextLine();
        resultHash = resultHash.replaceAll("\\s+", "");

        scanner.close();
    }

    public void readFile2() throws FileNotFoundException {
        File file = new File("src/main/resources/test-files/test-file2.txt");
        Scanner scanner = new Scanner(file);

        scanner.nextLine();
        plainText = scanner.nextLine();
        for (int i = 0; i < 8; ++i)
            scanner.nextLine();

        int blockIndex = Integer.parseInt(scanner.nextLine());
        hexValueWords.add(new ArrayList<>());
        scanner.nextLine();

        for (int i = 0; i < 16; ++i) {
            String line = scanner.nextLine();
            int spaceIndex = line.lastIndexOf(' ');
            String hex = line.substring(spaceIndex + 1);
            hexValueWords.get(blockIndex).add(hex);
        }
        scanner.nextLine();

        intermediateValues.add(new ArrayList<>());
        for (int i = 0; i < 80; ++i) {
            String line = scanner.nextLine();
            int index = line.indexOf(':');
            line = line.substring(index + 2);
            String[] strings = line.split("\\s+");
            intermediateValues.get(blockIndex).add(new ArrayList<>());
            for (String string : strings) {
                intermediateValues.get(blockIndex).get(i).add(string);
            }
        }

        for (int i = 0; i < 7; ++i)
            scanner.nextLine();

        blockIndex = Integer.parseInt(scanner.nextLine());
        hexValueWords.add(new ArrayList<>());
        scanner.nextLine();

        for (int i = 0; i < 16; ++i) {
            String line = scanner.nextLine();
            int spaceIndex = line.lastIndexOf(' ');
            String hex = line.substring(spaceIndex + 1);
            hexValueWords.get(blockIndex).add(hex);
        }
        scanner.nextLine();

        intermediateValues.add(new ArrayList<>());
        for (int i = 0; i < 80; ++i) {
            String line = scanner.nextLine();
            int index = line.indexOf(':');
            line = line.substring(index + 2);
            String[] strings = line.split("\\s+");
            intermediateValues.get(blockIndex).add(new ArrayList<>());
            for (String string : strings) {
                intermediateValues.get(blockIndex).get(i).add(string);
            }
        }

        for (int i = 0; i < 8; ++i)
            scanner.nextLine();

        resultHash = scanner.nextLine();
        resultHash = resultHash.replaceAll("\\s+", "");

        scanner.close();
    }

    /** Test which asserts that the length (of the hexadecimal string) of the hash from the SHA1Hasher is 40. */
    @Test
    public void testHashLength() {
        System.out.println("Running hash length test...");
        SHA1Hasher sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText("abc");
        sha1Hasher.hashPlainText();
        System.out.println("Hash: " + sha1Hasher.getResultHash());
        assertEquals(40, sha1Hasher.getResultHash().length());
    }

    /** Test which checks the intermediate values (of the test vector) for a SHA1Hasher run on the plainText "abc". */
    @Test
    public void testWithTestVector1() {
        System.out.println("Running test vector 1 test...");

        try {
            readFile1();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
        System.out.println(plainText);
        System.out.println("------------------");
        hexValueWords.forEach(list -> list.forEach(System.out::println));
        System.out.println("------------------");
        intermediateValues.forEach(list -> list.forEach(subList -> {
            System.out.print(subList.get(0) + " " + subList.get(1) + " " + subList.get(2) + " " + subList.get(3) + " " + subList.get(4) + "\n");
        }));
        System.out.println("------------------");
        System.out.println(resultHash.toLowerCase());
        */

        SHA1Hasher sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText(plainText);
        sha1Hasher.hashPlainText();
        System.out.println("Hash: " + sha1Hasher.getResultHash());

        for (int blockIndex = 0; blockIndex < hexValueWords.size(); ++blockIndex)
            for (int wordIndex = 0; wordIndex < hexValueWords.get(blockIndex).size(); ++wordIndex) {
                assertEquals(Long.parseLong(hexValueWords.get(blockIndex).get(wordIndex), 16),
                        Long.parseLong(sha1Hasher.getHexValueWords().get(blockIndex).get(wordIndex), 16));
            }

        for (int blockIndex = 0; blockIndex < intermediateValues.size(); ++blockIndex)
            for (int listIndex = 0; listIndex < intermediateValues.size(); ++listIndex) {
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(0), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(0), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(1), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(1), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(2), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(2), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(3), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(3), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(4), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(4), 16));
            }

        assertEquals(resultHash.toLowerCase(), sha1Hasher.getResultHash().toLowerCase());
    }

    /** Test which checks the intermediate values (of the test vector) for a SHA1Hasher run on the plainText "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq". */
    @Test
    public void testWithTestVector2() {
        System.out.println("Running test vector 2 test...");

        try {
            readFile2();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
        System.out.println(plainText);
        System.out.println("------------------");
        hexValueWords.forEach(list -> list.forEach(System.out::println));
        System.out.println("------------------");
        intermediateValues.forEach(list -> list.forEach(subList -> {
            System.out.print(subList.get(0) + " " + subList.get(1) + " " + subList.get(2) + " " + subList.get(3) + " " + subList.get(4) + "\n");
        }));
        System.out.println("------------------");
        System.out.println(resultHash.toLowerCase());
        */

        SHA1Hasher sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText(plainText);
        sha1Hasher.hashPlainText();
        System.out.println("Hash: " + sha1Hasher.getResultHash());

        for (int blockIndex = 0; blockIndex < hexValueWords.size(); ++blockIndex) {
            for (int wordIndex = 0; wordIndex < hexValueWords.get(blockIndex).size(); ++wordIndex) {
                assertEquals(Long.parseLong(hexValueWords.get(blockIndex).get(wordIndex), 16),
                        Long.parseLong(sha1Hasher.getHexValueWords().get(blockIndex).get(wordIndex), 16));
            }
        }

        for (int blockIndex = 0; blockIndex < intermediateValues.size(); ++blockIndex) {
            for (int listIndex = 0; listIndex < intermediateValues.size(); ++listIndex) {
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(0), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(0), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(1), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(1), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(2), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(2), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(3), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(3), 16));
                assertEquals(Long.parseLong(intermediateValues.get(blockIndex).get(listIndex).get(4), 16),
                        Long.parseLong(sha1Hasher.getIntermediateValues().get(blockIndex).get(listIndex).get(4), 16));
            }
        }

        assertEquals(resultHash.toLowerCase(), sha1Hasher.getResultHash().toLowerCase());
    }

    /** Test which checks the cascade effect of the SHA1Hasher by hashing two similar (but not equal) plainTexts
     * and calculating the Hamming distance between their hashes. */
    @Test
    public void testHammingDistance() {
        System.out.println("Running cascade (hamming distance) test...");

        String inputText = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        SHA1Hasher sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText(inputText);
        sha1Hasher.hashPlainText();
        System.out.println("Hash: " + sha1Hasher.getResultHash());

        String resultHash1 = new BigInteger(sha1Hasher.getResultHash(), 16).toString(2);
        resultHash1 = BitStringHandler.padRightBitStringWithZeros(resultHash1, 160);

        inputText = "abcdbczecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"; // changed the 7th character to a 'z'
        sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText(inputText);
        sha1Hasher.hashPlainText();
        System.out.println("Hash: " + sha1Hasher.getResultHash());

        String resultHash2 = new BigInteger(sha1Hasher.getResultHash(), 16).toString(2);
        resultHash2 = BitStringHandler.padRightBitStringWithZeros(resultHash2, 160);

        System.out.println("Hamming distance between the two outputs: " + BitStringHandler.calculateHammingDistance(resultHash1, resultHash2));
        assertTrue(((double) BitStringHandler.calculateHammingDistance(resultHash1, resultHash2)) / resultHash1.length() > HAMMING_DIST_MARGIN);
    }
}