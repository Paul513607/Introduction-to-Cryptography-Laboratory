package generators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.Compressor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class BBSGeneratorTest {
    private final int MAX_BITSET_LEN = 10000;
    private final double CLOSENESS_FACTOR = 5.0;
    private final double COMPRESSION_RATE_TARGET = 2.0;
    private String outputBitString;
    private BitSet outputBitSet = new BitSet(1000);

    @BeforeEach
    public void bbsGenerate() {
        BBSGenerator bbsGenerator = new BBSGenerator();
        bbsGenerator.calculateOutput();
        outputBitString = new String();
        outputBitString = bbsGenerator.getOutputBitString();
        outputBitSet = new BitSet();
        outputBitSet = bbsGenerator.getOutputBitSet();
    }

    @Test
    public void frequencyTest() {
        double counter1 = 0, counter0 = 0;
        for (int index = 0; index < outputBitString.length(); ++index) {
            if (outputBitString.charAt(index) == '1')
                counter1++;
            else
                counter0++;
        }
        counter0 = counter0 / outputBitString.length() * 100.0;
        counter1 = counter1 / outputBitString.length() * 100.0;

        Assertions.assertTrue(Math.abs(counter0 - counter1) <= CLOSENESS_FACTOR);
        System.out.println(counter0);
        System.out.println(counter1);
    }

    @Test
    public void compressionRateTest() throws IOException {
        Compressor compressor = new Compressor(outputBitString, "/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBScompressionTest/");
        try {
            compressor.compressDefault();
            compressor.compressInputString();
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        Path pathDefault = Paths.get("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBScompressionTest/defaultTest.zip");
        Path path = Paths.get("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBScompressionTest/test.zip");

        assertTrue(Math.floor((double) Files.size(path) / (double) Files.size(pathDefault)) > COMPRESSION_RATE_TARGET);
    }
}