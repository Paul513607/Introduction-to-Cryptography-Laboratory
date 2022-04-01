package generators;

import lombok.Data;
import utilities.RunSettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Data
public class LFSRGenerator {
    public final int REGISTER_LENGTH;
    public final List<Integer> coefficientsList;

    private List<Boolean> currBitList;
    private String outputBitString;

    // test related attributes
    private List<Boolean> firstBitList;
    private Integer generatorPeriod = 0;

    public LFSRGenerator(int registerLength) {
        this.REGISTER_LENGTH = registerLength;
        this.coefficientsList = new ArrayList<>(ConnectionPolynomialMapper.connectionPolynomialMap.get(REGISTER_LENGTH));
        this.currBitList = new ArrayList<>(REGISTER_LENGTH);
        generateInitialConfig();

    }

    private void generateInitialConfig() {
        Random random = new Random();
        boolean allZeros = true;
        for (int index = 0; index < REGISTER_LENGTH; index++) {
            currBitList.add(random.nextBoolean());
            if (currBitList.get(index))
                allZeros = false;
        }

        // retry if we generated all zeros
        if (allZeros) {
            currBitList.clear();
            generateInitialConfig();
        }
        else {
            firstBitList = new ArrayList<>(currBitList);
        }
    }

    public void generateOutputBitString() {
        StringBuilder outputStringBuilder = new StringBuilder();

        for (int iteration = 0; iteration < RunSettings.MAX_ITERATIONS; ++iteration) {
            int currInt = currBitList.get(REGISTER_LENGTH - 1) ? 1 : 0;
            outputStringBuilder.append(currInt);

            // calculate the first bit for the next shift
            boolean xorBoolean = false;
            for (Integer coefficient : coefficientsList)
                xorBoolean = xorBoolean ^ currBitList.get(coefficient - 1);

            // shift the bit list to the right and set the first bit
            for (int i = currBitList.size() - 2; i >= 0; --i) {
                currBitList.set(i + 1, currBitList.get(i));
            }
            currBitList.set(0, xorBoolean);

            // test related operation
            if (currBitList.equals(firstBitList) && generatorPeriod == 0) {
                // we add 1 since we start at iteration 0 but check with the bitList from iteration 1
                generatorPeriod = iteration + 1;
            }
        }

        outputBitString = outputStringBuilder.toString();
    }

    public void writeNumberToDefaultFile() {
        File defaultFile = new File("/home/paul/Facultate/An2/Semestru2/IC/Introduction-to-Cryptography-Laboratory/HW3/LFSR-Generator/src/main/resources/LFSR-Generator-output/iterations" + RunSettings.MAX_ITERATIONS + "regLen" + REGISTER_LENGTH + ".txt");
        try {
            if (defaultFile.createNewFile())
                System.out.println("Created file: " + defaultFile.getPath());
            else
                System.out.println("File: " + defaultFile.getPath() + " already exists. Will overwrite it.");
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        try {
            FileWriter defaultFileWriter = new FileWriter(defaultFile);
            defaultFileWriter.write(outputBitString);
            defaultFileWriter.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void printNumber() {
        System.out.println("The generator number is:");
        System.out.println(outputBitString);
    }
}
