import rc4cryptosystem.RC4Cryptosystem;

public class Main {
    public static void main(String[] args) {
        RC4Cryptosystem rc4Cryptosystem = new RC4Cryptosystem("HIIAMKEY", null, null);

        // Encryption
        rc4Cryptosystem.readTextFromFile("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW3/RC4-Cryptosystem/src/main/resources/decrypted-files/plainTextFile.txt", 0);
            // set the key stream
        rc4Cryptosystem.pseudoRandomGenerationAlgorithm();
        rc4Cryptosystem.encrypt();
        RC4Cryptosystem.writeTextToFile("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW3/RC4-Cryptosystem/src/main/resources/encrypted-files/encrypted1.txt", rc4Cryptosystem.getEncryptedText());
        System.out.println("Encryption done!");

        rc4Cryptosystem.setPlainText(null);
        rc4Cryptosystem.setEncryptedText(null);

        // Decryption
        rc4Cryptosystem.readTextFromFile("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW3/RC4-Cryptosystem/src/main/resources/encrypted-files/encrypted1.txt", 1);
            // set the key stream
        rc4Cryptosystem.pseudoRandomGenerationAlgorithm();
        rc4Cryptosystem.decrypt();
        RC4Cryptosystem.writeTextToFile("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW3/RC4-Cryptosystem/src/main/resources/decrypted-files/decrypted1.txt", rc4Cryptosystem.getPlainText());
        System.out.println("Decryption done!");

        rc4Cryptosystem.setPlainText(null);
        rc4Cryptosystem.setEncryptedText(null);
    }
}
