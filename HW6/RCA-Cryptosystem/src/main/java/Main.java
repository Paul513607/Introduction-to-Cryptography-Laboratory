import rsacryptosystem.RCACryptosystem;
import util.Timer;

public class Main {
    public void testRcaEncAndDec() {
        // Encryption
        RCACryptosystem rcaCryptosystem = new RCACryptosystem();
        rcaCryptosystem.readTextFromFile("src/main/resources/test-files/plainText.txt", 0);

        rcaCryptosystem.encryptPlainText();
        RCACryptosystem.writeTextToFile("src/main/resources/test-files/encryptedText.txt",
                rcaCryptosystem.getEncryptedText());

        Timer timer = Timer.getInstance();
        timer.start();

        // Decryption
        rcaCryptosystem.setPlainText(null);
        rcaCryptosystem.setEncryptedText(null);
        rcaCryptosystem.readTextFromFile("src/main/resources/test-files/encryptedText.txt", 1);

        rcaCryptosystem.decryptEncryptedText();
        RCACryptosystem.writeTextToFile("src/main/resources/test-files/decryptedText.txt",
                rcaCryptosystem.getPlainText());

        timer.stop();
        timer.showTimeTakenWithMessage("Decryption took:");
    }

    public static void main(String[] args) {
        Main main = new Main();

        main.testRcaEncAndDec();
    }
}
