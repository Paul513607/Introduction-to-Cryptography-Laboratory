import attack.WienerAttack;
import rsacryptosystem.RSACryptosystem;
import util.Timer;

public class Main {
    public void testRcaEncAndDec() {
        // Encryption
        RSACryptosystem rsaCryptosystem = new RSACryptosystem();
        rsaCryptosystem.generateKeys();
        rsaCryptosystem.readTextFromFile("src/main/resources/test-files/plainText.txt", 0);

        rsaCryptosystem.encryptPlainText();
        RSACryptosystem.writeTextToFile("src/main/resources/test-files/encryptedText.txt",
                rsaCryptosystem.getEncryptedText());

        // Decryption
        rsaCryptosystem.setPlainText(null);
        rsaCryptosystem.setEncryptedText(null);
        rsaCryptosystem.readTextFromFile("src/main/resources/test-files/encryptedText.txt", 1);

        rsaCryptosystem.decryptEncryptedText();
        RSACryptosystem.writeTextToFile("src/main/resources/test-files/decryptedText.txt",
                rsaCryptosystem.getPlainText());
    }

    public void testAttack() {
        WienerAttack wienerAttack = new WienerAttack();
        wienerAttack.runAttack(wienerAttack.getRsaCryptosystem().getN(), wienerAttack.getRsaCryptosystem().getE());
        wienerAttack.printSolution();
    }

    public static void main(String[] args) {
        Main main = new Main();

        // main.testRcaEncAndDec();
        main.testAttack();
    }
}
