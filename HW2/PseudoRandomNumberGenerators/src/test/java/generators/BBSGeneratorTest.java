package generators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.Compressor;
import utilities.RunSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class BBSGeneratorTest {
    private final int MAX_BITSET_LEN = RunSettings.MAX_ITERATIONS;
    private final double CLOSENESS_FACTOR = 5.0;
    private final double COMPRESSION_RATE_TARGET = 2.0;
    private String outputBitString;
    private BitSet outputBitSet = new BitSet(MAX_BITSET_LEN);

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

        System.out.println("0s counter: " + counter0);
        System.out.println("1s counter: " + counter1);
        Assertions.assertTrue(Math.abs(counter0 - counter1) <= CLOSENESS_FACTOR);
    }

    @Test
    public void compressionRateTest() throws IOException {
        Compressor compressor = new Compressor(outputBitString, "/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBSCompressionTest/");
        try {
            compressor.compressDefault();
            compressor.compressInputString();
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        Path pathDefault = Paths.get("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBSCompressionTest/defaultTest.zip");
        Path path = Paths.get("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW2/BBSCompressionTest/test.zip");

        System.out.println("BBS Generated compression: " + Files.size(path));
        System.out.println("Default compression: " + Files.size(pathDefault));
        assertTrue(Math.floor((double) Files.size(path) / (double) Files.size(pathDefault)) > COMPRESSION_RATE_TARGET);
    }
}