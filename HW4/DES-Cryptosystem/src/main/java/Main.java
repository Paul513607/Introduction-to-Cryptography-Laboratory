import attacks.MeetInTheMiddle2DESAttack;
import descryptosystem.*;
import util.BitStringConverter;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public void desTest() {
        // setup
        String key = "0001001100110100010101110111100110011011101111001101111111110001";
        System.out.println("Key: " + BitStringConverter.convertBitStringToMsg(key));

        DESCryptosystem desCryptosystem = new DESCryptosystem();
        desCryptosystem.generateSubKeys(key);

        desCryptosystem.readTextFromFile("src/main/resources/des-cryptosystem-files/plainTextFile.txt", 0);
        desCryptosystem.encryptText(desCryptosystem.getPlainText());
        DESCryptosystem.writeTextToFile("src/main/resources/des-cryptosystem-files/encryptedTextFile.txt", desCryptosystem.getEncryptedText());
        // System.out.println(desCryptosystem.getEncryptedText());

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);

        // setup
        desCryptosystem.readTextFromFile("src/main/resources/des-cryptosystem-files/encryptedTextFile.txt", 1);
        desCryptosystem.decryptText(desCryptosystem.getEncryptedText());
        DESCryptosystem.writeTextToFile("src/main/resources/des-cryptosystem-files/decryptedTextFile.txt", desCryptosystem.getPlainText());
        // System.out.println(desCryptosystem.getPlainText());

        desCryptosystem.setPlainText(null);
        desCryptosystem.setEncryptedText(null);
    }

    public void doubleDesTest() {
        // setup
        DoubleDESCryptosystem doubleDESCryptosystem = new DoubleDESCryptosystem();
        System.out.println("Key1: " + doubleDESCryptosystem.getKey1Formatted());
        System.out.println("Key2: " + doubleDESCryptosystem.getKey2Formatted());

        doubleDESCryptosystem.readTextFromFile("src/main/resources/des-cryptosystem-files/plainTextFile.txt", 0);
        doubleDESCryptosystem.encryptText(doubleDESCryptosystem.getPlainText());
        DoubleDESCryptosystem.writeTextToFile("src/main/resources/des-cryptosystem-files/encryptedTextFile.txt", doubleDESCryptosystem.getEncryptedText());
        // System.out.println(doubleDESCryptosystem.getEncryptedText());

        doubleDESCryptosystem.setPlainText(null);
        doubleDESCryptosystem.setEncryptedText(null);

        // setup
        doubleDESCryptosystem.readTextFromFile("src/main/resources/des-cryptosystem-files/encryptedTextFile.txt", 1);
        doubleDESCryptosystem.decryptText(doubleDESCryptosystem.getEncryptedText());
        DoubleDESCryptosystem.writeTextToFile("src/main/resources/des-cryptosystem-files/decryptedTextFile.txt", doubleDESCryptosystem.getPlainText());
        // System.out.println(doubleDESCryptosystem.getPlainText());

        doubleDESCryptosystem.setPlainText(null);
        doubleDESCryptosystem.setEncryptedText(null);
    }

    public void meetInTheMiddle2DESAttackTest() {
        // setup
        MeetInTheMiddle2DESAttack meetInTheMiddle2DESAttack = new MeetInTheMiddle2DESAttack();
        meetInTheMiddle2DESAttack.readTextFromFile("src/main/resources/des-cryptosystem-files/plainTextFile.txt", 0);
        meetInTheMiddle2DESAttack.readTextFromFile("src/main/resources/des-cryptosystem-files/encryptedTextFile.txt", 1);

        meetInTheMiddle2DESAttack.meetInTheMiddleAttack();
        for (int index = 0; index < meetInTheMiddle2DESAttack.getPossibleKey1().size(); ++index) {
            System.out.println("Key Pair:");
            System.out.println("\tKey 1: " + meetInTheMiddle2DESAttack.getPossibleKey1().get(index));
            System.out.println("\tKey 2: " + meetInTheMiddle2DESAttack.getPossibleKey2().get(index));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type an option (des, 2des or mmatk): ");
        String option = scanner.nextLine();
        switch (option) {
            case "des" -> main.desTest();
            case "2des" -> main.doubleDesTest();
            case "mmatk" -> main.meetInTheMiddle2DESAttackTest();
            default -> System.out.println("Unknown option");
        }
    }
}
