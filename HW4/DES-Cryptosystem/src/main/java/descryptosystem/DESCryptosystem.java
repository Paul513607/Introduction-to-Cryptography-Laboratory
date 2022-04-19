package descryptosystem;

import lombok.Data;
import util.BitStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
public class DESCryptosystem {
    String initialKey;
    List<String> generatedSubKeys = new ArrayList<>();
    String plainText;
    String encryptedText;

    public DESCryptosystem() {
    }

    private void validateKey(String inputKey) throws IllegalArgumentException {
        String inputKeyString = BitStringConverter.convertBitStringToMsg(inputKey);
        if (inputKeyString.length() != 8)
            throw new IllegalArgumentException("Error for inputKey. The test key size must be 8 bytes (64 bits).");
    }

    private String getInitialKeyPermutation(String inputKey) {
        StringBuilder permutation = new StringBuilder();
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 7; ++j) {
                int index = TablesConstants.pc1[i][j];
                permutation.append(inputKey.charAt(index - 1));
            }
        return permutation.toString();
    }

    public void generateSubKeys(String inputKey) {
        validateKey(inputKey);

        initialKey = inputKey;
        generatedSubKeys.clear();
        String inputKeyPermutation = getInitialKeyPermutation(inputKey);
        String subKeyFirstHalf0 = inputKeyPermutation.substring(0, inputKeyPermutation.length() / 2);
        String subKeySecondHalf0 = inputKeyPermutation.substring(inputKeyPermutation.length() / 2);

        String prevSubKeyFirstHalf = subKeyFirstHalf0;
        String prevSubKeySecondHalf = subKeySecondHalf0;

        for (int iteration = 1; iteration <= 16; ++iteration) {
            String currSubKeyFirstHalf = "";
            String currSubKeySecondHalf = "";

            String prev0 = prevSubKeyFirstHalf;
            String prev1 = prevSubKeySecondHalf;

            for (int shift = 1; shift <= TablesConstants.iterationToNoShifts.get(iteration); ++shift) {
                currSubKeyFirstHalf = prev0.substring(1);
                currSubKeySecondHalf = prev1.substring(1);

                char firstHalfChar = prev0.charAt(0);
                char secondHalfChar = prev1.charAt(0);

                currSubKeyFirstHalf += firstHalfChar;
                currSubKeySecondHalf += secondHalfChar;

                prev0 = currSubKeyFirstHalf;
                prev1 = currSubKeySecondHalf;
            }

            prevSubKeyFirstHalf = currSubKeyFirstHalf;
            prevSubKeySecondHalf = currSubKeySecondHalf;

            String generatedKeyTemp = currSubKeyFirstHalf + currSubKeySecondHalf;
            generatedSubKeys.add(generatedKeyTemp);
        }

        formSubKeys();
    }

    private void formSubKeys() {
        int counter = 0;
        for (String subKey : generatedSubKeys) {
            StringBuilder permSubKey = new StringBuilder();
            for (int i = 0; i < 8; ++i)
                for (int j = 0; j < 6; ++j) {
                    int index = TablesConstants.pc2[i][j];
                    permSubKey.append(subKey.charAt(index - 1));
                }
            generatedSubKeys.set(counter, permSubKey.toString());
            counter++;
        }
    }

    private String getInitialPlainBlockPermutation(String block) {
        StringBuilder blockPermutation = new StringBuilder();
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                int index = TablesConstants.ip[i][j];
                blockPermutation.append(block.charAt(index - 1));
            }
        return blockPermutation.toString();
    }

    public void encryptText(String inputText) {
        encryptedText = "";
        StringBuilder encryptedTextBuilder = new StringBuilder();
        int blockIndex;
        for (blockIndex = 0; blockIndex + 8 < inputText.length(); blockIndex += 8) {
            String blockString = inputText.substring(blockIndex, blockIndex + 8);

            String blockBitString = BitStringConverter.convertMsgToBitString(blockString);
            String encryptedBlockBitString = encryptBlock(blockBitString);

            encryptedTextBuilder.append(BitStringConverter.convertBitStringToMsg(encryptedBlockBitString));
        }

        encryptedText = encryptedTextBuilder.toString();

        String remainingBlock;
        if (blockIndex > 8)
            remainingBlock = inputText.substring(blockIndex - 8);
        else
            remainingBlock = inputText;

        if (remainingBlock.length() > 0) {
            remainingBlock = remainingBlock + " ".repeat(Math.max(0, 8 - remainingBlock.length()));

            String blockBitString = BitStringConverter.convertMsgToBitString(remainingBlock);

            String encryptedBlockBitString = encryptBlock(blockBitString);

            encryptedText = encryptedText + BitStringConverter.convertBitStringToMsg(encryptedBlockBitString);
        }
    }

    private String encryptBlock(String block) {
        String blockPerm = getInitialPlainBlockPermutation(block);

        String blockFirstHalf0 = blockPerm.substring(0, blockPerm.length() / 2);
        String blockSecondHalf0 = blockPerm.substring(blockPerm.length() / 2);

        String prevBlockFirstHalf = blockFirstHalf0;
        String prevBlockSecondHalf = blockSecondHalf0;

        for (int round = 1; round <= 16; ++round) {
            String currBlockFirstHalf = prevBlockSecondHalf;
            String currBlockSecondHalf = TablesConstants.xorBitStrings(prevBlockFirstHalf, feistelFunc(prevBlockSecondHalf, generatedSubKeys.get(round - 1)));

            prevBlockFirstHalf = currBlockFirstHalf;
            prevBlockSecondHalf = currBlockSecondHalf;
        }

        String tempEncryptedBlock = prevBlockSecondHalf + prevBlockFirstHalf;

        return getFinalPermutation(tempEncryptedBlock);
    }

    public void decryptText(String inputEncryptedText) {
        plainText = "";
        StringBuilder decryptedTextBuilder = new StringBuilder();
        int blockIndex;
        for (blockIndex = 0; blockIndex + 8 < inputEncryptedText.length(); blockIndex += 8) {
            String blockString = inputEncryptedText.substring(blockIndex, blockIndex + 8);

            String blockBitString = BitStringConverter.convertMsgToBitString(blockString);
            String decryptedBlockBitString = decryptBlock(blockBitString);

            decryptedTextBuilder.append(BitStringConverter.convertBitStringToMsg(decryptedBlockBitString));
        }

        plainText = decryptedTextBuilder.toString();

        String remainingBlock;
        if (blockIndex > 8)
            remainingBlock = inputEncryptedText.substring(blockIndex - 8);
        else
            remainingBlock = inputEncryptedText;

        if (remainingBlock.length() > 0) {
            remainingBlock = remainingBlock + " ".repeat(Math.max(0, 8 - remainingBlock.length()));

            String blockBitString = BitStringConverter.convertMsgToBitString(remainingBlock);

            String decryptedBlockBitString = decryptBlock(blockBitString);

            plainText = plainText + BitStringConverter.convertBitStringToMsg(decryptedBlockBitString);
        }
    }

    public String decryptBlock(String encryptedBlock) {
        String blockPerm = getInitialPlainBlockPermutation(encryptedBlock);

        String blockFirstHalf0 = blockPerm.substring(0, blockPerm.length() / 2);
        String blockSecondHalf0 = blockPerm.substring(blockPerm.length() / 2);

        String prevBlockFirstHalf = blockFirstHalf0;
        String prevBlockSecondHalf = blockSecondHalf0;

        for (int round = 16; round >= 1; --round) {
            String currBlockFirstHalf = prevBlockSecondHalf;
            String currBlockSecondHalf = TablesConstants.xorBitStrings(prevBlockFirstHalf, feistelFunc(prevBlockSecondHalf, generatedSubKeys.get(round - 1)));

            prevBlockFirstHalf = currBlockFirstHalf;
            prevBlockSecondHalf = currBlockSecondHalf;
        }

        String tempDecryptedBlock = prevBlockSecondHalf + prevBlockFirstHalf;

        return getFinalPermutation(tempDecryptedBlock);
    }

    private String getFinalPermutation(String tempEncryptedBlock) {
        StringBuilder blockPermutation = new StringBuilder();
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                int index = TablesConstants.ipInv[i][j];
                blockPermutation.append(tempEncryptedBlock.charAt(index - 1));
            }
        return blockPermutation.toString();
    }

    private String feistelFunc(String halfOfBlock, String subKey) {
        String expanded = expandBlock(halfOfBlock);
        expanded = TablesConstants.xorBitStrings(subKey, expanded);

        String sBoxesResult = getSBoxesResult(expanded);
        String feistelFinalPermResult = getFeistelFinalPermutation(sBoxesResult);

        return feistelFinalPermResult;
    }

    private String getFeistelFinalPermutation(String sBoxesResult) {
        StringBuilder feistelFinalPerm = new StringBuilder();
        for (int i = 0 ; i < 8; ++i)
            for (int j = 0; j < 4; ++j) {
                int index = TablesConstants.fPermutation[i][j];
                feistelFinalPerm.append(sBoxesResult.charAt(index - 1));
            }
        return feistelFinalPerm.toString();
    }

    private String getSBoxesResult(String inputExpandedString) {
        List<String> subBitStrings = new ArrayList<>();
        for (int index = 0; index < inputExpandedString.length(); index += 6) {
            String temp = inputExpandedString.substring(index, index + 6);
            subBitStrings.add(temp);
        }
        StringBuilder sBoxesResultBuilder = new StringBuilder();

        String curr = subBitStrings.get(0);
        String rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        String colNoStr = curr.substring(1, curr.length() - 1);
        int row = Integer.parseInt(rowNoStr, 2);
        int col = Integer.parseInt(colNoStr, 2);
        int number = TablesConstants.s1Box[row][col];
        String s1Box = Integer.toBinaryString(number);
        StringBuilder s1Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s1Box.length(); fill++) {
            s1Builder.append('0');
        }
        s1Builder.append(s1Box);
        sBoxesResultBuilder.append(s1Builder);

        curr = subBitStrings.get(1);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s2Box[row][col];
        String s2Box = Integer.toBinaryString(number);
        StringBuilder s2Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s2Box.length(); fill++) {
            s2Builder.append('0');
        }
        s2Builder.append(s2Box);
        sBoxesResultBuilder.append(s2Builder);

        curr = subBitStrings.get(2);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s3Box[row][col];
        String s3Box = Integer.toBinaryString(number);
        StringBuilder s3Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s3Box.length(); fill++) {
            s3Builder.append('0');
        }
        s3Builder.append(s3Box);
        sBoxesResultBuilder.append(s3Builder);

        curr = subBitStrings.get(3);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s4Box[row][col];
        String s4Box = Integer.toBinaryString(number);
        StringBuilder s4Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s4Box.length(); fill++) {
            s4Builder.append('0');
        }
        s4Builder.append(s4Box);
        sBoxesResultBuilder.append(s4Builder);

        curr = subBitStrings.get(4);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s5Box[row][col];
        String s5Box = Integer.toBinaryString(number);
        StringBuilder s5Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s5Box.length(); fill++) {
            s5Builder.append('0');
        }
        s5Builder.append(s5Box);
        sBoxesResultBuilder.append(s5Builder);

        curr = subBitStrings.get(5);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s6Box[row][col];
        String s6Box = Integer.toBinaryString(number);
        StringBuilder s6Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s6Box.length(); fill++) {
            s6Builder.append('0');
        }
        s6Builder.append(s6Box);
        sBoxesResultBuilder.append(s6Builder);

        curr = subBitStrings.get(6);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s7Box[row][col];
        String s7Box = Integer.toBinaryString(number);
        StringBuilder s7Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s7Box.length(); fill++) {
            s7Builder.append('0');
        }
        s7Builder.append(s7Box);
        sBoxesResultBuilder.append(s7Builder);

        curr = subBitStrings.get(7);
        rowNoStr = "" + curr.charAt(0) + curr.charAt(curr.length() - 1);
        colNoStr = curr.substring(1, curr.length() - 1);
        row = Integer.parseInt(rowNoStr, 2);
        col = Integer.parseInt(colNoStr, 2);
        number = TablesConstants.s8Box[row][col];
        String s8Box = Integer.toBinaryString(number);
        StringBuilder s8Builder = new StringBuilder();
        for (int fill = 0; fill < 4 - s8Box.length(); fill++) {
            s8Builder.append('0');
        }
        s8Builder.append(s8Box);
        sBoxesResultBuilder.append(s8Builder);

        return sBoxesResultBuilder.toString();
    }

    private String expandBlock(String halfOfBlock) {
        StringBuilder expandedBlock = new StringBuilder();
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 6; ++j) {
                int index = TablesConstants.eBit[i][j];
                expandedBlock.append(halfOfBlock.charAt(index - 1));
            }
        return expandedBlock.toString();
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

    public static void writeTextToFile(String path, String text) {
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
