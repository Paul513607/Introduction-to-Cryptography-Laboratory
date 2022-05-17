package rsacryptosystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RSACryptosystemTest {

    /** Test for checking if decrypt(encrypt(text)) = text. */
    @Test
    public void testRcaEncAndDec() {
        // Encryption
        RSACryptosystem rsaCryptosystem = new RSACryptosystem();
        rsaCryptosystem.generateKeys();
        rsaCryptosystem.readTextFromFile("src/main/resources/test-files/plainText.txt", 0);
        String plainText = rsaCryptosystem.getPlainText();

        rsaCryptosystem.encryptPlainText();
        RSACryptosystem.writeTextToFile("src/main/resources/test-files/encryptedText.txt",
                rsaCryptosystem.getEncryptedText());
        String encryptedText = rsaCryptosystem.getEncryptedText();

        // Decryption
        rsaCryptosystem.setPlainText(null);
        rsaCryptosystem.setEncryptedText(null);
        rsaCryptosystem.readTextFromFile("src/main/resources/test-files/encryptedText.txt", 1);

        rsaCryptosystem.decryptEncryptedText();
        RSACryptosystem.writeTextToFile("src/main/resources/test-files/decryptedText.txt",
                rsaCryptosystem.getPlainText());
        String decryptedText = rsaCryptosystem.getPlainText();

        assertEquals(plainText, decryptedText);
    }
}