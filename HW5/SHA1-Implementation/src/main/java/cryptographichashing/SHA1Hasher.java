package cryptographichashing;

import lombok.Data;
import lombok.NoArgsConstructor;
import util.BitStringHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
@NoArgsConstructor
public class SHA1Hasher {
    private static int TWO_TO_32 = ((int) Math.pow(2, 32));

    private String plainText;
    private int bitTextLength;
    private String bitStringMessage;
    private String resultHash;

    private static int H0 = 0x67452301;
    private static int H1 = 0xEFCDAB89;
    private static int H2 = 0x98BADCFE;
    private static int H3 = 0x10325476;
    private static int H4 = 0xC3D2E1F0;

    private static int K0 = 0x5A827999;
    private static int K1 = 0x6ED9EBA1;
    private static int K2 = 0x8F1BBCDC;
    private static int K3 = 0xCA62C1D6;

    // test variables
    private List<List<String>> hexValueWords = new ArrayList<>();
    private List<List<List<String>>> intermediateValues = new ArrayList<>();

    public void readPlainTextFromUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input a text to be hashed: ");
        plainText = scanner.nextLine();
        scanner.close();
    }

    public void readPlainTextFromFile(String path) {
        File file = new File(path);

        try {
            plainText = Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPreProcessing() {
        bitTextLength = plainText.length() * 8;
        bitStringMessage = BitStringHandler.convertMsgToBitString(plainText) + '1';

        StringBuilder bitStringMessageBuilder = new StringBuilder(bitStringMessage);
        while (bitStringMessageBuilder.length() % 512 != 448) {
            bitStringMessageBuilder.append('0');
        }

        String msgLengthAsBitString = BitStringHandler.integerToBinaryString(bitTextLength);
        msgLengthAsBitString = BitStringHandler.padRightBitStringWithZeros(msgLengthAsBitString, 64);

        bitStringMessageBuilder.append(msgLengthAsBitString);
        bitStringMessage = bitStringMessageBuilder.toString();
    }

    public void processMessageAsChunks() {
        // break the bit string message into 512-bit chunks, and process each of them
        for (int initialIndex = 0; initialIndex < bitStringMessage.length(); initialIndex += 512) {
            String currChunk = bitStringMessage.substring(initialIndex, initialIndex + 512);

            // break the chunk into 16 32-bit words
            List<String> wordsList = new ArrayList<>();
            hexValueWords.add(new ArrayList<>());
            for (int wordIndex = 0; wordIndex < currChunk.length(); wordIndex += 32) {
                String currWord = currChunk.substring(wordIndex, wordIndex + 32);
                wordsList.add(currWord);
                hexValueWords.get(initialIndex / 512).add(BitStringHandler.binaryToHexString(currWord));
            }

            // extend the 16 words into 80 words
            for (int i = 16; i < 80; ++i) {
                wordsList.add("");
                String result = BitStringHandler.xorBitStrings(
                        BitStringHandler.xorBitStrings(wordsList.get(i - 3), wordsList.get(i - 8)),
                        BitStringHandler.xorBitStrings(wordsList.get(i - 14), wordsList.get(i - 16))
                );
                result = leftRotateBitString(result, 1);

                wordsList.set(i, result);
            }


            // initialize hash values for the current chunk
            String a = BitStringHandler.padRightBitStringWithZeros(BitStringHandler.hexToBinaryString(SHA1Hasher.H0), 32);
            String b = BitStringHandler.padRightBitStringWithZeros(BitStringHandler.hexToBinaryString(SHA1Hasher.H1), 32);
            String c = BitStringHandler.padRightBitStringWithZeros(BitStringHandler.hexToBinaryString(SHA1Hasher.H2), 32);
            String d = BitStringHandler.padRightBitStringWithZeros(BitStringHandler.hexToBinaryString(SHA1Hasher.H3), 32);
            String e = BitStringHandler.padRightBitStringWithZeros(BitStringHandler.hexToBinaryString(SHA1Hasher.H4), 32);
            intermediateValues.add(new ArrayList<>());

            // main loop
            for (int i = 0; i < 80; ++i) {
                String bitWiseFunctionResult;
                String kBitString;
                intermediateValues.get(initialIndex / 512).add(new ArrayList<>());

                if (i < 20) {
                    bitWiseFunctionResult = bitWiseFunction1(b, c, d);
                    kBitString = BitStringHandler.hexToBinaryString(SHA1Hasher.K0);
                }
                else if (i < 40) {
                    bitWiseFunctionResult = bitWiseFunction2(b, c, d);
                    kBitString = BitStringHandler.hexToBinaryString(SHA1Hasher.K1);
                }
                else if (i < 60) {
                    bitWiseFunctionResult = bitWiseFunction3(b, c, d);
                    kBitString = BitStringHandler.hexToBinaryString(SHA1Hasher.K2);
                }
                else {
                    bitWiseFunctionResult = bitWiseFunction2(b, c, d);
                    kBitString = BitStringHandler.hexToBinaryString(SHA1Hasher.K3);
                }

                int temp = (int) (BitStringHandler.binaryStringToInteger(leftRotateBitString(a, 5)) +
                                        BitStringHandler.binaryStringToInteger(bitWiseFunctionResult) +
                                        BitStringHandler.binaryStringToInteger(e) +
                                        BitStringHandler.binaryStringToInteger(kBitString) +
                                        BitStringHandler.binaryStringToInteger(wordsList.get(i)));

                e = d;
                d = c;
                c = leftRotateBitString(b, 30);
                b = a;
                a = BitStringHandler.integerToBinaryString(temp);

                a = BitStringHandler.padRightBitStringWithZeros(a, 32);
                b = BitStringHandler.padRightBitStringWithZeros(b, 32);
                c = BitStringHandler.padRightBitStringWithZeros(c, 32);
                d = BitStringHandler.padRightBitStringWithZeros(d, 32);
                e = BitStringHandler.padRightBitStringWithZeros(e, 32);

                /*
                System.out.println("--------------------------------------");
                System.out.println("Iteration " + i + ": ");
                System.out.println(BitStringHandler.binaryToHexString(a));
                System.out.println(BitStringHandler.binaryToHexString(b));
                System.out.println(BitStringHandler.binaryToHexString(c));
                System.out.println(BitStringHandler.binaryToHexString(d));
                System.out.println(BitStringHandler.binaryToHexString(e));
                System.out.println("--------------------------------------");
                */

                intermediateValues.get(initialIndex / 512).get(i).add(BitStringHandler.binaryToHexString(a));
                intermediateValues.get(initialIndex / 512).get(i).add(BitStringHandler.binaryToHexString(b));
                intermediateValues.get(initialIndex / 512).get(i).add(BitStringHandler.binaryToHexString(c));
                intermediateValues.get(initialIndex / 512).get(i).add(BitStringHandler.binaryToHexString(d));
                intermediateValues.get(initialIndex / 512).get(i).add(BitStringHandler.binaryToHexString(e));
            }

            // add this chunk's hash to the result
            SHA1Hasher.H0 = (int) (SHA1Hasher.H0 + Long.parseLong(a, 2));
            SHA1Hasher.H1 = (int) (SHA1Hasher.H1 + Long.parseLong(b, 2));
            SHA1Hasher.H2 = (int) (SHA1Hasher.H2 + Long.parseLong(c, 2));
            SHA1Hasher.H3 = (int) (SHA1Hasher.H3 + Long.parseLong(d, 2));
            SHA1Hasher.H4 = (int) (SHA1Hasher.H4 + Long.parseLong(e, 2));
        }

        String h1Length = Integer.toHexString(SHA1Hasher.H0);
        String h2Length = Integer.toHexString(SHA1Hasher.H1);
        String h3Length = Integer.toHexString(SHA1Hasher.H2);
        String h4Length = Integer.toHexString(SHA1Hasher.H3);
        String h5Length = Integer.toHexString(SHA1Hasher.H4);

        // add leading 0's
        if(h1Length.length() < 8) {
            StringBuilder h1L = new StringBuilder(h1Length);
            h1L.insert(0,0);
            h1Length = h1L.toString();
        } else if(h2Length.length() < 8) {
            StringBuilder h2L = new StringBuilder(h2Length);
            h2L.insert(0,0);
            h2Length = h2L.toString();
        } else if(h3Length.length() < 8) {
            StringBuilder h3L = new StringBuilder(h3Length);
            h3L.insert(0,0);
            h3Length = h3L.toString();
        } else if(h4Length.length() < 8) {
            StringBuilder h4L = new StringBuilder(h4Length);
            h4L.insert(0,0);
            h4Length = h4L.toString();
        } else if(h5Length.length() < 8) {
            StringBuilder h5L = new StringBuilder(h5Length);
            h5L.insert(0,0);
            h5Length = h5L.toString();
        }

        resultHash = h1Length + h2Length + h3Length + h4Length + h5Length;
    }

    private String bitWiseFunction1(String b, String c, String d) {
        return BitStringHandler.orBitStrings(
                BitStringHandler.andBitStrings(b, c),
                BitStringHandler.andBitStrings(
                        BitStringHandler.notBitString(b),
                        d));
    }

    private String bitWiseFunction2(String b, String c, String d) {
        return BitStringHandler.xorBitStrings(
                BitStringHandler.xorBitStrings(b, c),
                d);
    }

    private String bitWiseFunction3(String b, String c, String d) {
        return BitStringHandler.orBitStrings(
                BitStringHandler.orBitStrings(
                        BitStringHandler.andBitStrings(b, c),
                        BitStringHandler.andBitStrings(b, d)),
                BitStringHandler.andBitStrings(c, d)
        );
    }

    private String leftRotateBitString(String bitString, int howManyTimes) {
        String subString = bitString.substring(0, howManyTimes);
        return bitString.substring(howManyTimes) + subString;
    }

    private String leftShiftHexValue(int hexValue, int howManyTimes) {
        String hexValueBitString = Integer.toBinaryString(hexValue);
        System.out.println(hexValueBitString.length());
        return hexValueBitString.substring(howManyTimes);
    }

    public void hashPlainText() {
        doPreProcessing();
        processMessageAsChunks();
        System.out.println("Hash: " + resultHash);
    }
}
