import cryptographichashing.*;

public class Main {
    public void testSha1Hashing() {
        SHA1Hasher hasher = new SHA1Hasher();

        // read plainText to be hashed
        hasher.readPlainTextFromUserInput();
        // hasher.readPlainTextFromFile("src/main/resources/hasher-input-files/plainText-file1.txt");
        hasher.hashPlainText();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.testSha1Hashing();
    }
}
