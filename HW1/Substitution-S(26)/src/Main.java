import substitutioncrypting.*;

import java.io.File;

public class Main {
    static void testEncryption() {
        File testFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/substitutionTestFiles/testFile.txt");
        SubstitutionEncrypter substitutionEncrypter = new SubstitutionEncrypter(testFile, RunSettings.encryptSet);

        substitutionEncrypter.encryptPlainText();

        // substitutionEncrypter.printEncryptedTextAndSubstitutionTable();
        File fileToWriteTo = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/substitutionTestFiles/testFileEncrypted.txt");
        substitutionEncrypter.printEncryptedTextToFile(fileToWriteTo);
    }

    static void testDecryption() {
        File testFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/substitutionTestFiles/testFileEncrypted.txt");
        SubstitutionDecrypter substitutionDecrypter = new SubstitutionDecrypter(testFile);

        substitutionDecrypter.decryptText();

        // substitutionDecrypter.printDecryptedTextAndSubstitutionTable();
        File fileToWriteTo = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW1/substitutionTestFiles/testFileDecrypted.txt");
        substitutionDecrypter.printSubstitutionTable();
        substitutionDecrypter.printDecryptedTextToFile(fileToWriteTo);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java Main.java <encrypt / e / decrypt / d> <[second_option]>\n" +
                    "\t(if \"encrypt/e\" selected specify [gen] after \"encrypt/e\" for random substitution table generation)");
            return;
        }
        if (args[0].equals("encrypt") || args[0].equals("e")) {
            if (args.length > 1) {
                if (args[1].equals("gen")) {
                    RunSettings.encryptSet = 1;
                }
                else {
                    RunSettings.encryptSet = 0;
                }
            }
            testEncryption();
        }
        else if (args[0].equals("decrypt") || args[0].equals("d")) {
            testDecryption();
        }
    }
}
