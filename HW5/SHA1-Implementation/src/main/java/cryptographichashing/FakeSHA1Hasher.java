package cryptographichashing;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/** Class which hashes a plainText with the SHA-1 algorithm and saves only the 32 bits of the final hash. */
@Data
@NoArgsConstructor
public class FakeSHA1Hasher {
    private String plainText;
    private String resultHash;

    public void readPlainTextFromUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input a text to be hashed: ");
        plainText = scanner.nextLine();
        scanner.close();
    }

    public void readPlainTextFromFile(String path) {
        File file = new File(path);

        try {
            plainText = Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hashPlainText() {
        SHA1Hasher sha1Hasher = new SHA1Hasher();
        sha1Hasher.setPlainText(plainText);
        sha1Hasher.hashPlainText();
        resultHash = sha1Hasher.getResultHash().substring(0, 4); // keep only the first 32 bits of the original hashed string (i.e. the first 8 hexadecimal values)
    }
}
