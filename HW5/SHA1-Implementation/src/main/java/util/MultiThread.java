package util;

import attacks.BirthdayAttack;
import cryptographichashing.FakeSHA1Hasher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiThread implements Callable<Map<String, String>> {
    private static final int NO_OF_GENERATIONS_THREAD = BirthdayAttack.NO_OF_GENERATIONS / 32;   // MAX_NO_THREADS = 32;

    private String initialText;
    private String initialTextBitString;

    @Override
    public Map<String, String> call() throws Exception {
        FakeSHA1Hasher fakeSHA1Hasher;
        Map<String, String> hashToMessage = new HashMap<>();

        for (int generation = 0; generation < NO_OF_GENERATIONS_THREAD; ++generation) {
            String currString = BitStringHandler.convertBitStringToMsg(BirthdayAttack.makeMinorModificationsToMessage(initialTextBitString));
            fakeSHA1Hasher = new FakeSHA1Hasher();
            fakeSHA1Hasher.setPlainText(currString);
            fakeSHA1Hasher.hashPlainText();

            hashToMessage.put(fakeSHA1Hasher.getResultHash(), currString);
        }

        return hashToMessage;
    }
}
