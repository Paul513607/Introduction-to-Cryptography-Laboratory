package attacks;

import cryptographichashing.FakeSHA1Hasher;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BirthdayAttackLogger;
import util.BitStringHandler;
import util.MultiThread;
import util.TryCounter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Class which implements a birthday attack on the FakeSHA1Hasher (simplified SHA-1 hasher). */
@Data
@NoArgsConstructor
public class BirthdayAttack implements Serializable {
    public static final int HASH_BIT_SIZE = 32;
    public static final int NO_OF_GENERATIONS = ((int) Math.pow(2, HASH_BIT_SIZE / 2.0));

    private String legitMessage;
    private String fraudulentMessage;

    private String legitMessageBitString;
    private String fraudulentMessageBitString;

    private Map<String, String> hashToValueMap = new HashMap<>();

    public void setLegitMessage(String legitMessage) {
        this.legitMessage = legitMessage;
        legitMessageBitString = BitStringHandler.convertMsgToBitString(legitMessage);
    }

    public void setFraudulentMessage(String fraudulentMessage) {
        this.fraudulentMessage = fraudulentMessage;
        fraudulentMessageBitString = BitStringHandler.convertMsgToBitString(fraudulentMessage);
    }

    public static String makeMinorModificationsToMessage(String messageBitString) {
        Random random = new Random();
        int howManyModifications = random.nextInt(1, 6);

        StringBuilder builder = new StringBuilder(messageBitString);
        for (int i = 0; i < howManyModifications; ++i) {
            int randomPos = random.nextInt(0, messageBitString.length());
            builder.setCharAt(randomPos, (char) ('1' - builder.charAt(randomPos) + '0'));   // swap the bit on randomPos
        }

        return builder.toString();
    }

    public void setUpHashesForLegitMessageModifications() {
        System.out.println(NO_OF_GENERATIONS);
        FakeSHA1Hasher fakeSHA1Hasher;

        fakeSHA1Hasher = new FakeSHA1Hasher();
        fakeSHA1Hasher.setPlainText(legitMessage);
        fakeSHA1Hasher.hashPlainText();
        hashToValueMap.put(fakeSHA1Hasher.getResultHash(), legitMessage);

        while (hashToValueMap.keySet().size() < NO_OF_GENERATIONS) {
            String currString = BitStringHandler.convertBitStringToMsg(makeMinorModificationsToMessage(legitMessageBitString));
            fakeSHA1Hasher = new FakeSHA1Hasher();
            fakeSHA1Hasher.setPlainText(currString);
            fakeSHA1Hasher.hashPlainText();

            hashToValueMap.put(fakeSHA1Hasher.getResultHash(), currString);
        }

        System.out.println(hashToValueMap.keySet().size());
        System.out.println("Done with set up.");
    }

    public void threadedSetUpHashesForLegitMessageModifications() {
        FakeSHA1Hasher fakeSHA1Hasher;

        fakeSHA1Hasher = new FakeSHA1Hasher();
        fakeSHA1Hasher.setPlainText(legitMessage);
        fakeSHA1Hasher.hashPlainText();
        hashToValueMap.put(fakeSHA1Hasher.getResultHash(), legitMessage);

        List<Future<Map<String, String>>> resultFutures = new ArrayList<>();

        ExecutorService service = Executors.newFixedThreadPool(32);

        for (int threadNo = 0; threadNo < 16; ++threadNo) {
            MultiThread multiThread = new MultiThread(legitMessage, legitMessageBitString);
            Future<Map<String, String>> currFuture = service.submit(multiThread);
            resultFutures.add(currFuture);
        }

        for (int threadNo = 0; threadNo < 16; ++threadNo) {
            Future<Map<String, String>> future = resultFutures.get(threadNo);
            try {
                Map<String, String> currResult = future.get();
                currResult.keySet()
                        .forEach(key -> hashToValueMap.put(key, currResult.get(key)));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        service.shutdown();
        System.out.println("Done with set up.");
    }

    public void searchForCollisionsInFraudulentMessageModifications() {
        FakeSHA1Hasher fakeSHA1Hasher;

        fakeSHA1Hasher = new FakeSHA1Hasher();
        fakeSHA1Hasher.setPlainText(fraudulentMessage);
        fakeSHA1Hasher.hashPlainText();

        if (hashToValueMap.containsKey(fakeSHA1Hasher.getResultHash())) {
            System.out.println("--------------------------");
            System.out.println("Found collision on hash: " + fakeSHA1Hasher.getResultHash());
            System.out.println();
            System.out.println("Legitimate text modification: " + hashToValueMap.get(fakeSHA1Hasher.getResultHash()));
            System.out.println();
            System.out.println("Fraudulent text modification: " + fraudulentMessage);
            System.out.println("--------------------------");

            System.out.println("\nDone with searching.");
            return;
        }
        TryCounter.hashToTextSearch.put(fakeSHA1Hasher.getResultHash(), fraudulentMessage);

        boolean matchFound = false;
        while (!matchFound) {
            String currString = BitStringHandler.convertBitStringToMsg(makeMinorModificationsToMessage(fraudulentMessageBitString));

            fakeSHA1Hasher = new FakeSHA1Hasher();
            fakeSHA1Hasher.setPlainText(currString);
            fakeSHA1Hasher.hashPlainText();

            if (hashToValueMap.containsKey(fakeSHA1Hasher.getResultHash())) {
                System.out.println("--------------------------");
                System.out.println("Found collision on hash: " + fakeSHA1Hasher.getResultHash());
                System.out.println();
                System.out.println("Legitimate text modification: " + hashToValueMap.get(fakeSHA1Hasher.getResultHash()));
                System.out.println();
                System.out.println("Fraudulent text modification: " + currString);
                System.out.println("--------------------------");
                matchFound = true;
            }

            TryCounter.hashToTextSearch.put(fakeSHA1Hasher.getResultHash(), currString);
        }

        System.out.println("\nDone with searching.");
    }

    public void readTextFromFile(String path, int option) {
        File file = new File(path);

        String text = "";
        try {
            text = Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (option == 0) {
            legitMessage = text;
            legitMessageBitString = BitStringHandler.convertMsgToBitString(legitMessage);
        }
        else if (option == 1) {
            fraudulentMessage = text;
            fraudulentMessageBitString = BitStringHandler.convertMsgToBitString(fraudulentMessage);
        }
    }

    public void saveData() {
        BirthdayAttackLogger.saveBirthdayAttackData(this);
    }
}
