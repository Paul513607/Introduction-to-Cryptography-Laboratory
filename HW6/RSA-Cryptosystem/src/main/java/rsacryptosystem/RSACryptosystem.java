package rsacryptosystem;

import lombok.Data;
import util.BitStringHandler;
import util.Timer;
import util.UtilCalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** RCA implementation for both encryption and decryption. */
@Data
public class RSACryptosystem {
    private final int MAX_BIT_SIZE = 1024;
    private BigInteger P, Q;
    private BigInteger N, phiN;
    private BigInteger E = BigInteger.valueOf(2).pow(16).add(BigInteger.valueOf(1));
    private BigInteger D;

    private String plainText;
    private String encryptedText;

    public RSACryptosystem() {
    }

    public RSACryptosystem(String plainText, String encryptedText) {
        this.plainText = plainText;
        this.encryptedText = encryptedText;
    }

    private void chooseE() {
        Random rnd = new Random();
        E = new BigInteger(32, rnd);
        while (E.compareTo(BigInteger.valueOf(1)) != 1 || E.compareTo(phiN) != -1 ||
                !UtilCalculator.areNumbersCoPrime(E, phiN)) {
            E = new BigInteger(32, rnd);
        }
    }

    public void chooseD() {
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

    public void generateKeys() {
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
        BigInteger blockBigInt = new BigInteger(blockBitString, 2);
        Map<BigInteger, BigInteger> powerToValueMod = new HashMap<>();

        powerToValueMod.put(BigInteger.ZERO, BigInteger.ONE);
        powerToValueMod.put(BigInteger.ONE, blockBigInt.mod(N));
        for (BigInteger i = BigInteger.TWO; i.compareTo(D) <= 0; i = i.multiply(BigInteger.TWO)) {
            BigInteger prevResult = powerToValueMod.get(i.divide(BigInteger.TWO));
            powerToValueMod.put(i, prevResult.multiply(prevResult).mod(N));
        }

        String bitStringD = D.toString(2);

        BigInteger result = BigInteger.ONE;
        BigInteger fact = BigInteger.ONE;
        for (int index = bitStringD.length() - 1; index >= 0; index--) {
            if (bitStringD.charAt(index) == '1') {
                result = result.multiply(powerToValueMod.get(fact)).mod(N);
            }

            fact = fact.multiply(BigInteger.TWO);
        }

        String resultBitString = result.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }
    // Decryption took: 0.052477671s (seconds).

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

        BigInteger pExp = D.mod(P.subtract(BigInteger.ONE));
        BigInteger decryptP = blockBigInt.mod(P);
        decryptP = decryptP.modPow(pExp, P);

        BigInteger qExp = D.mod(Q.subtract(BigInteger.ONE));
        BigInteger decryptQ = blockBigInt.mod(Q);
        decryptQ = decryptQ.modPow(qExp, Q);

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
        if (remainder2.equals(BigInteger.ZERO)) {
            remainder2 = remainder1;
        } else {
            if (remainder2.compareTo(BigInteger.ZERO) < 0) {
                BigInteger temp = remainder2.abs();
                temp = temp.mod(minPrime);
                temp = temp.negate();
                remainder2 = minPrime.add(temp);
            }
            // maxPrimeSimplified * X = remainder mod minPrime
            // modular inverse of maxPrimeSimplified is maxPrimeSimplified^(minPrime - 2) since minPrime is prime
            // by Fermat's little theorem
            remainder2 = remainder2.multiply(maxPrime.modPow(minPrime.subtract(BigInteger.TWO), minPrime)).mod(minPrime);
        }

        String resultBitString = remainder2.toString(2);
        int remainingLen = resultBitString.length() % 8;
        if (remainingLen % 8 != 0) {
            resultBitString = "0".repeat(8 - remainingLen) + resultBitString;
        }

        return BitStringHandler.convertBitStringToMsg(resultBitString);
    }
    // Decryption took: 0.003296043s (seconds).

    public void decryptEncryptedText() {
        StringBuilder decryptedTextBuilder = new StringBuilder();
        int blockIndex;
        // break the text into 2048-bit blocks
        for (blockIndex = 0; blockIndex + 256 < encryptedText.length(); blockIndex += 256) {
            String block = encryptedText.substring(blockIndex, blockIndex + 256);

            String resultText = decryptBlockDummy(block);
            // String resultText = decryptBlock(block);
            resultText = decryptBlockCRT(block);
            decryptedTextBuilder.append(resultText);
        }

        // treat the final block as well
        String block = encryptedText.substring(blockIndex);

        Timer timer = Timer.getInstance();
        timer.start();

        String resultText = decryptBlockDummy(block);
        // String resultText = decryptBlock(block);

        timer.stop();
        timer.showTimeTakenWithMessage("Duration for 'decryptBlockDummy': ");

        timer.start();

        resultText = decryptBlockCRT(block);
        decryptedTextBuilder.append(resultText);

        timer.stop();
        timer.showTimeTakenWithMessage("Duration for 'decryptBlockCRT': ");

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
