package generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Date;
import java.util.Formatter;
import java.util.Random;

public class JacobiGenerator implements Generator {
    private final int MAX_BITSET_LEN = 10000;
    private BigInteger bigIntegerN;
    private BigInteger seed0;
    private String outputBitString = new String();
    private BitSet outputBitSet = new BitSet(MAX_BITSET_LEN);

    public JacobiGenerator() {
        setupGenerator();
    }

    public JacobiGenerator(BigInteger bigIntegerN, BigInteger seed0) {
        this.bigIntegerN = bigIntegerN;
        this.seed0 = seed0;
    }

    public BigInteger getBigIntegerN() {
        return bigIntegerN;
    }

    public void setBigIntegerN(BigInteger bigIntegerN) {
        this.bigIntegerN = bigIntegerN;
    }

    public BigInteger getSeed0() {
        return seed0;
    }

    public void setSeed0(BigInteger seed0) {
        this.seed0 = seed0;
    }

    @Override
    public String getOutputBitString() {
        return outputBitString;
    }

    public BitSet getOutputBitSet() {
        return outputBitSet;
    }

    @Override
    public String toString() {
        return "JacobiGenerator{" +
                "bigIntegerN=" + bigIntegerN +
                ", seed0=" + seed0 +
                ", outputBitString='" + outputBitString + '\'' +
                '}';
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String encryptText(String text) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(text.getBytes(StandardCharsets.UTF_8));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    @Override
    public void setupGenerator() {
        Random random = new SecureRandom();
        BigInteger firstPrime = BigInteger.probablePrime(512, random);
        while (!firstPrime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            firstPrime = BigInteger.probablePrime(512, random);
        }
        BigInteger secondPrime = BigInteger.probablePrime(512, random);
        while (!secondPrime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            secondPrime = BigInteger.probablePrime(512, random);
        }

        bigIntegerN = firstPrime.multiply(secondPrime);

        Date date = new Date();
        long seed0time = date.getTime();

        seed0 = BigInteger.valueOf(seed0time);
        seed0 = seed0.multiply(seed0);
        seed0 = seed0.mod(bigIntegerN);
    }

    private static int calcJacobiSymbol(BigInteger a, BigInteger bigOdd) {
        BigInteger b = a.mod(bigOdd);
        BigInteger c = bigOdd;
        int s = 1;
        while (b.compareTo(BigInteger.valueOf(2)) >= 0) {
            while (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(0)))
                b = b.divide(BigInteger.valueOf(4));
            if (b.mod(BigInteger.valueOf(2)).equals(BigInteger.valueOf(0))) {
                BigInteger remainderAsBig = c.mod(BigInteger.valueOf(8));
                int remainder = remainderAsBig.intValue();
                if (remainder == 3 || remainder == 5)
                    s = -s;
                b = b.divide(BigInteger.valueOf(2));
            }
            if (b.equals(BigInteger.valueOf(1)))
                break;
            if (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) &&
                c.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                s = -s;
            }
            BigInteger bCopy = b;
            b = c.mod(b);
            c = bCopy;
        }
        return s * b.intValue();
    }

    @Override
    public void calculateOutput() {
        StringBuilder outputBitStringBuilder = new StringBuilder();

        for (int index = 0; index < MAX_BITSET_LEN; ++index) {
            BigInteger seedCurr = seed0.add(BigInteger.valueOf(index));
            int jacobiCalc = calcJacobiSymbol(seedCurr, bigIntegerN);
            if (jacobiCalc == -1)
                jacobiCalc++;
            outputBitStringBuilder.append(jacobiCalc);
        }

        outputBitString = outputBitStringBuilder.toString();
        for (int i = 0; i < outputBitString.length(); ++i)
            if (outputBitString.charAt(i) == '1')
                outputBitSet.set(i);
    }

    @Override
    public void writeNumberToDefaultFile() {
        File defaultFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/jacobiNumber.txt");
        try {
            if (defaultFile.createNewFile())
                System.out.println("Created file: " + defaultFile.getPath());
            else
                System.out.println("File: " + defaultFile.getPath() + " already exists. Will overwrite it.");
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileWriter defaultFileWriter = new FileWriter(defaultFile);
            defaultFileWriter.write(outputBitString);
            defaultFileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }
}
