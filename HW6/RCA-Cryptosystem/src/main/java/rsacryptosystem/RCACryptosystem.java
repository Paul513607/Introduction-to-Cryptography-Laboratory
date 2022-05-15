package rsacryptosystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BitStringHandler;
import util.UtilCalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/** RCA implementation for both encryption and decryption. */
@Data
public class RCACryptosystem {
    private final int MAX_BIT_SIZE = 64;
    private BigInteger P, Q;
    private BigInteger N, phiN;
    private BigInteger E = BigInteger.valueOf(2).pow(16).add(BigInteger.valueOf(1));
    private BigInteger D;

    private String plainText;
    private String encryptedText;

    public RCACryptosystem() {
        generateKeys();
    }

    public RCACryptosystem(String plainText, String encryptedText) {
        this.plainText = plainText;
        this.encryptedText = encryptedText;

        generateKeys();
    }

    private void chooseE() {
        Random rnd = new Random();
        E = new BigInteger(32, rnd);
        while (E.compareTo(BigInteger.valueOf(1)) != 1 || E.compareTo(phiN) != -1 ||
                !UtilCalculator.areNumbersCoPrime(E, phiN)) {
            E = new BigInteger(32, rnd);
        }
    }

    private void chooseD() {
        boolean foundD = false;
        for (long i = 1; !foundD; ++i) {
            BigInteger intermediateVal = phiN.multiply(BigInteger.valueOf(i))
                    .add(BigInteger.valueOf(1));

            if (intermediateVal.remainder(E).compareTo(BigInteger.valueOf(0)) == 0) {
                foundD = true;
                D = intermediateVal.divide(E);
            }
        }
    }

    private void generateKeys() {
        // Setup two different 1024-bit prime numbers and N, their product
        Random rnd = new Random();
        P = BigInteger.probablePrime(MAX_BIT_SIZE, rnd);
        Q = BigInteger.probablePrime(MAX_BIT_SIZE, rnd);
        while (Q.equals(P)) {
            Q = BigInteger.probablePrime(MAX_BIT_SIZE, rnd);
        }
        N = P.multiply(Q);
        phiN = P.subtract(BigInteger.valueOf(1)).multiply(
                Q.subtract(BigInteger.valueOf(1)));

        // chooseE();
        chooseD();
    }

    private String encryptBlock(String block) {
        String blockBitString = BitStringHandler.convertMsgToBitString(block);
        BigInteger blockBigInt = new BigInteger(blockBitString, 2);
        BigInteger resultBigInt = blockBigInt.modPow(E, N);

        String resultBitString = resultBigInt.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }

    public void encryptPlainText() {
        StringBuilder encryptedTextBuilder = new StringBuilder();
        int blockIndex;
        // break the text into 2048-bit blocks
        for (blockIndex = 0; blockIndex + 256 < plainText.length(); blockIndex += 256) {
            String block = plainText.substring(blockIndex, blockIndex + 256);

            String resultText = encryptBlock(block);
            encryptedTextBuilder.append(resultText);
        }

        // treat the final block as well
        String block = plainText.substring(blockIndex);

        String resultText = encryptBlock(block);
        encryptedTextBuilder.append(resultText);

        encryptedText = encryptedTextBuilder.toString();
        System.out.println("Done with encryption!");
    }

    private String decryptBlockDummy(String block) {
        String blockBitString = BitStringHandler.convertMsgToBitString(block);

        BigInteger resultBigInt = new BigInteger(blockBitString, 2);
        for (BigInteger i = BigInteger.ZERO; i.compareTo(D) < 0; i = i.add(BigInteger.ONE)) {
            resultBigInt = resultBigInt.multiply(resultBigInt).mod(N);
        }

        String resultBitString = resultBigInt.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }

    private String decryptBlock(String block) {
        String blockBitString = BitStringHandler.convertMsgToBitString(block);
        BigInteger blockBigInt = new BigInteger(blockBitString, 2);
        BigInteger resultBigInt = blockBigInt.modPow(D, N);

        String resultBitString = resultBigInt.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }

    private String decryptBlockCRT(String block) {
        String blockBitString = BitStringHandler.convertMsgToBitString(block);
        BigInteger blockBigInt = new BigInteger(blockBitString, 2);

        BigInteger pExp = D.mod(P.subtract(BigInteger.valueOf(1)));
        BigInteger decryptP = blockBigInt.mod(P).modPow(pExp, P);

        BigInteger qExp = D.mod(Q.subtract(BigInteger.valueOf(1)));
        BigInteger decryptQ = blockBigInt.mod(Q).modPow(qExp, Q);

        BigInteger maxPrime, remainder1, minPrime, remainder2;
        if (P.compareTo(Q) > 0) {
            maxPrime = P;
            remainder1 = decryptP;
            minPrime = Q;
            remainder2 = decryptQ;
        } else {
            maxPrime = Q;
            remainder1 = decryptQ;
            minPrime = P;
            remainder2 = decryptP;
        }

        maxPrime = maxPrime.mod(minPrime);
        // maxPrime * X + remainder1 = remainder2 mod minPrime
        remainder2 = remainder2.subtract(remainder1);
        if (remainder2.compareTo(BigInteger.ZERO) < 0)
            remainder2 = minPrime.add(remainder2);

        // maxPrimeSimplified * X = remainder mod minPrime
        for (BigInteger myNum = BigInteger.ONE; myNum.compareTo(minPrime) < 0;
             myNum = myNum.add(BigInteger.ONE)) {
            if (maxPrime.multiply(myNum).mod(minPrime).equals(BigInteger.ZERO)) {
                remainder2 = remainder2.multiply(myNum).mod(minPrime);
                break;
            }
        }


        String resultBitString = remainder2.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }

    public void decryptEncryptedText() {
        StringBuilder decryptedTextBuilder = new StringBuilder();
        int blockIndex;
        // break the text into 2048-bit blocks
        for (blockIndex = 0; blockIndex + 256 < encryptedText.length(); blockIndex += 256) {
            String block = encryptedText.substring(blockIndex, blockIndex + 256);

            String resultText = decryptBlockDummy(block);
            // String resultText = decryptBlock(block);
            // String resultText = decryptBlockCRT(block);
            decryptedTextBuilder.append(resultText);
        }

        // treat the final block as well
        String block = encryptedText.substring(blockIndex);

        String resultText = decryptBlockDummy(block);
        // String resultText = decryptBlock(block);
        // String resultText = decryptBlockCRT(block);
        decryptedTextBuilder.append(resultText);

        plainText = decryptedTextBuilder.toString();
        System.out.println("Done with decryption!");
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
