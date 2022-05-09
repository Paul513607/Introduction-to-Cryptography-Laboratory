import attacks.BirthdayAttack;
import cryptographichashing.*;
import util.BirthdayAttackLogger;
import util.Timer;

public class Main {
    public void testSha1Hashing() {
        SHA1Hasher hasher = new SHA1Hasher();

        // read plainText to be hashed
        // hasher.readPlainTextFromUserInput();
        hasher.readPlainTextFromFile("src/main/resources/hasher-input-files/plainText-file1.txt");
        hasher.hashPlainText();
        System.out.println("Hash: " + hasher.getResultHash());
    }

    public void testBirthdayAttackOnFakeSHA1Hasher() {
        // set up the attack
        BirthdayAttack birthdayAttack = new BirthdayAttack();

        /*
        // birthdayAttack.setLegitMessage("abc");
        // birthdayAttack.setFraudulentMessage("xyz");
        birthdayAttack.readTextFromFile("src/main/resources/hasher-input-files/legitMessage-file1.txt", 0);
        birthdayAttack.readTextFromFile("src/main/resources/hasher-input-files/fraudulentMessage-file1.txt", 1);
        birthdayAttack.setUpHashesForLegitMessageModifications();
        birthdayAttack.saveData();
        */

        birthdayAttack.readTextFromFile("src/main/resources/hasher-input-files/legitMessage-file1.txt", 0);
        birthdayAttack.readTextFromFile("src/main/resources/hasher-input-files/fraudulentMessage-file1.txt", 1);
        birthdayAttack.threadedSetUpHashesForLegitMessageModifications();
        // birthdayAttack = BirthdayAttackLogger.loadBirthdayAttackData();

        // start the attack to find collisions
        birthdayAttack.searchForCollisionsInFraudulentMessageModifications();
    }

    public static void main(String[] args) {
        Main main = new Main();

        Timer timer = Timer.getInstance();
        timer.start();

        // main.testSha1Hashing();
        main.testBirthdayAttackOnFakeSHA1Hasher();

        timer.stop();
        timer.showTimeTaken();
    }
}
