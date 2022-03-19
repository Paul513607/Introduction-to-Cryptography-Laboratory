package generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;

public class BBSGenerator implements Generator {
    private final int MAX_BITSET_LEN = 10000;
    private BigInteger bigIntegerN;
    private BigInteger seed0;
    private String outputBitString = new String();
    private BitSet outputBitSet = new BitSet(MAX_BITSET_LEN);

    public BBSGenerator() {
        setupGenerator();
    }

    public BBSGenerator(BigInteger bigIntegerN, BigInteger seed0) {
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
        return "BBSGenerator{" +
                "bigIntegerN=" + bigIntegerN +
                ", seed0=" + seed0 +
                '}';
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

    @Override
    public void calculateOutput() {
        BigInteger seedPrev = seed0;
        StringBuilder outputBitStringBuilder = new StringBuilder();
        outputBitStringBuilder.append(seed0.mod(BigInteger.valueOf(2)));
        int index = 0;
        for (int i = 0; i < MAX_BITSET_LEN; ++i) {
            BigInteger seedNext = seedPrev.multiply(seedPrev).mod(bigIntegerN);
            outputBitStringBuilder.append(seedNext.mod(BigInteger.valueOf(2)));
            seedPrev = seedNext;
        }

        outputBitString = outputBitStringBuilder.toString();
        for (int i = 0; i < outputBitString.length(); ++i)
            if (outputBitString.charAt(i) == '1')
                outputBitSet.set(i);
    }

    @Override
    public void writeNumberToDefaultFile() {
        File defaultFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/bbsNumber.txt");
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
