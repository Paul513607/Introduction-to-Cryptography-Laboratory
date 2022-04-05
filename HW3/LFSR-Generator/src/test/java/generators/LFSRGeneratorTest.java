package generators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LFSRGeneratorTest {
    public int REGISTER_LENGTH = 16;
    public LFSRGenerator lfsrGenerator;

    @BeforeEach
    public void runGenerator() {
        lfsrGenerator = new LFSRGenerator(REGISTER_LENGTH);
        lfsrGenerator.generateOutputBitString();
    }

    @Test
    public void checkIfRightPeriod() {
        assertTrue(lfsrGenerator.getGeneratorPeriod() == Math.pow(2, REGISTER_LENGTH) - 1);
    }

    @AfterEach
    public void printGeneratorPeriod() {
        System.out.println("Generator period: " + lfsrGenerator.getGeneratorPeriod());
    }
}