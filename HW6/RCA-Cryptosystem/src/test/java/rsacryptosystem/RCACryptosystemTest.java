package rsacryptosystem;

import org.junit.jupiter.api.Test;
import util.Timer;

import static org.junit.jupiter.api.Assertions.*;

class RCACryptosystemTest {

    /** Test for checking if decrypt(encrypt(text)) = text. */
    @Test
    public void testRcaEncAndDec() {
        // Encryption
        RCACryptosystem rcaCryptosystem = new RCACryptosystem();
        rcaCryptosystem.readTextFromFile("src/main/resources/test-files/plainText.txt", 0);
        String plainText = rcaCryptosystem.getPlainText();

        rcaCryptosystem.encryptPlainText();
        RCACryptosystem.writeTextToFile("src/main/resources/test-files/encryptedText.txt",
                rcaCryptosystem.getEncryptedText());
        String encryptedText = rcaCryptosystem.getEncryptedText();

        // Decryption
        rcaCryptosystem.setPlainText(null);
        rcaCryptosystem.setEncryptedText(null);
        rcaCryptosystem.readTextFromFile("src/main/resources/test-files/encryptedText.txt", 1);

        rcaCryptosystem.decryptEncryptedText();
        RCACryptosystem.writeTextToFile("src/main/resources/test-files/decryptedText.txt",
                rcaCryptosystem.getPlainText());
        String decryptedText = rcaCryptosystem.getPlainText();

        assertEquals(plainText, decryptedText);
    }
}