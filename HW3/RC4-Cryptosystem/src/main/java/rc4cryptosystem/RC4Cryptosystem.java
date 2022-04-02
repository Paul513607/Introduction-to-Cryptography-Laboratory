package rc4cryptosystem;

import lombok.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

@Data
public class RC4Cryptosystem {
    public static final int STREAM_SIZE = 256;
    public static final int SECRET_KEY_LENGTH = 8;

    private String plainText;
    private String encryptedText;

    private String secretKey;
    private String keyStream;

    public RC4Cryptosystem(String secretKey, String plainText, String encryptedText) throws IllegalArgumentException {
        if (secretKey.length() != SECRET_KEY_LENGTH)
            throw new IllegalArgumentException("The secret key must be of length " + SECRET_KEY_LENGTH + "!");
        this.secretKey = secretKey;
        if (plainText != null)
            this.plainText = plainText;
        if (encryptedText != null)
            this.encryptedText = encryptedText;
    }

    public ArrayList<Integer> keySchedulingAlgorithm() {
        ArrayList<Integer> permutation = new ArrayList<>(STREAM_SIZE);
        for (int i = 0; i < STREAM_SIZE; ++i)
            permutation.add(i);

        int j = 0;
        for (int i = 0; i < STREAM_SIZE; ++i) {
            j = (j + permutation.get(i) + secretKey.charAt(i % SECRET_KEY_LENGTH)) % STREAM_SIZE;
            Collections.swap(permutation, i, j);
        }

        return permutation;
    }

    public void pseudoRandomGenerationAlgorithm() {
        StringBuilder keyStreamBuilder = new StringBuilder();

        int i = 0;
        int j = 0;
        ArrayList<Integer> permutation = keySchedulingAlgorithm();

        int lengthToGenerate = 0;
        if (plainText != null)
            lengthToGenerate = plainText.length();
        else if (encryptedText != null)
            lengthToGenerate = encryptedText.length();

        for (int counter = 0; counter < lengthToGenerate; ++counter) {
            i = (i + 1) % STREAM_SIZE;
            j = (j + permutation.get(i)) % STREAM_SIZE;
            Collections.swap(permutation, i, j);

            int outputByte = permutation.get((permutation.get(i) + permutation.get(j)) % STREAM_SIZE);
            keyStreamBuilder.append((char) outputByte);
        }

        keyStream = keyStreamBuilder.toString();
    }

    public void encrypt() {
        StringBuilder encryptedTextBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); ++i) {
            char encryptedByte = (char) (plainText.charAt(i) ^ keyStream.charAt(i));
            encryptedTextBuilder.append(encryptedByte);
        }

        encryptedText = encryptedTextBuilder.toString();
    }

    public void decrypt() {
        StringBuilder plainTextBuilder = new StringBuilder();

        for (int i = 0; i < encryptedText.length(); ++i) {
            char decryptedByte = (char) (encryptedText.charAt(i) ^ keyStream.charAt(i));
            plainTextBuilder.append(decryptedByte);
        }

        plainText = plainTextBuilder.toString();
    }

    public void readTextFromFile(String path, int option) {
        File file = new File(path);

        String text = "";
        try {
            text = Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (option == 0)
            plainText = text;
        else if (option == 1)
            encryptedText = text;
    }

    public static void writeTextToFile (String path, String text) {
        File file = new File(path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
