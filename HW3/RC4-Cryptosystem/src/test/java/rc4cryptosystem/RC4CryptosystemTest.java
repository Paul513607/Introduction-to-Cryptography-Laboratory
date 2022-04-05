package rc4cryptosystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RC4CryptosystemTest {
    private static final int PLAIN_TEXT_LENGTH = 10;
    private static final int MAX_ITERATIONS = 100000;
    private static final double EPSILON = 0.001;

    private RC4Cryptosystem rc4Cryptosystem;
    private String secretKey = "HIIAMKEY";
    private String keyStream;
    private String plainText;

    @BeforeEach
    public void setUpRC4Cryptosystem() {
        StringBuilder plainTextBuilder = new StringBuilder();
        for (int i = 0; i < PLAIN_TEXT_LENGTH; ++i)
            plainTextBuilder.append("0");
        plainText = plainTextBuilder.toString();

        rc4Cryptosystem = new RC4Cryptosystem(secretKey, plainText, null);
    }

    private void generateRandomKey() {
        Random random = new Random();
        StringBuilder secretKeyBuilder = new StringBuilder();

        for (int i = 0; i < RC4Cryptosystem.SECRET_KEY_LENGTH; ++i) {
            int charVal = random.nextInt(0, 256);
            secretKeyBuilder.append((char) charVal);
        }

        secretKey = secretKeyBuilder.toString();
    }

    @Test
    public void secondByteIsOften0BiasTest() {
        double zeroCounter = 0;
        for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {

            generateRandomKey();
            rc4Cryptosystem.setSecretKey(secretKey);
            rc4Cryptosystem.pseudoRandomGenerationAlgorithm();

            if (rc4Cryptosystem.getKeyStream().charAt(1) == 0)
                zeroCounter++;
        }

        System.out.println("0 counter: " + zeroCounter);
        System.out.println("Probability: " + zeroCounter / MAX_ITERATIONS);
        System.out.println("Error from actual: " + Math.abs(zeroCounter / MAX_ITERATIONS - 0.0078125));

        assertTrue(Math.abs(zeroCounter / MAX_ITERATIONS - 0.0078125) <= EPSILON);
    }
}