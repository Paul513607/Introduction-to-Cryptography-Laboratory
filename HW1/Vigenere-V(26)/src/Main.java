import VigenereCrypting.RunSettings;
import VigenereCrypting.VigenereEncrypter;
import VigenereCrypting.VigenereDecrypter;

import java.io.File;

public class Main {
    static void testEncryption() {
        File testFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/textFiles/testFile.txt");
        VigenereEncrypter vigenereEncrypter = new VigenereEncrypter(testFile, RunSettings.encryptSet);

        vigenereEncrypter.encryptPlainText();

        // vigenereEncrypter.printEncryptedText();
        File fileToWriteTo = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/textFiles/testFileEncrypted.txt");
        vigenereEncrypter.printEncryptedTextToFile(fileToWriteTo);
    }

    static void testDecryption() {
        File testFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/textFiles/testFileEncrypted.txt");
        VigenereDecrypter vigenereDecrypter = new VigenereDecrypter(testFile);

        vigenereDecrypter.decryptText();

        // vigenereDecrypter.printDecryptedTextAndKey();
        File fileToWriteTo = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/textFiles/testFileDecrypted.txt");
        vigenereDecrypter.printKey();
        vigenereDecrypter.printDecryptedTextToFile(fileToWriteTo);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java Main.java <encrypt / e / decrypt / d> <[second_option]>\n" +
                    "\t(if \"encrypt/e\" selected specify [gen] after \"encrypt/e\" for random keygen)\n" +
                    "\t(if \"decrypt/d\" selected specify [ic/exh] after \"decrypt/e\" for a index_of_coincidence / a more exhaustive key search)");
            return;
        }
        if (args[0].equals("encrypt") || args[0].equals("e")) {
            if (args.length > 1) {
                RunSettings.encryptSet = args[1].equals("gen") ? 1 : 0;
            }
            testEncryption();
        }
        else if (args[0].equals("decrypt") || args[0].equals("d")) {
            if (args.length > 1) {
                RunSettings.decryptSet = args[1].equals("exh") ? 1 : 0;
            }
            testDecryption();
        }
    }
}
