package utilities;

import generators.BBSGenerator;
import generators.JacobiGenerator;

import java.math.BigInteger;
import java.util.concurrent.Callable;

public class MultiThread implements Callable<String> {
    int threadNumber;
    int threadOption;
    int lengthToGenerate;
    String threadResult;
    BBSGenerator bbsGenerator;
    JacobiGenerator jacobiGenerator;

    public MultiThread(int threadNumber, int threadOption, int lengthToGenerate, BBSGenerator bbsGenerator, JacobiGenerator jacobiGenerator) {
        this.threadNumber = threadNumber;
        this.threadOption = threadOption;
        this.lengthToGenerate = lengthToGenerate;
        this.bbsGenerator = bbsGenerator;
        this.jacobiGenerator = jacobiGenerator;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public String getThreadResult() {
        return threadResult;
    }

    @Override
    public String call() throws Exception {
        StringBuilder outputBitStringBuilder = new StringBuilder();

        if (threadOption == 1) {
            BigInteger seedPrev = bbsGenerator.getSeed0();
            outputBitStringBuilder.append(bbsGenerator.getSeed0().mod(BigInteger.valueOf(2)));

            for (int i = 0; i < lengthToGenerate; ++i) {
                BigInteger seedNext = seedPrev.multiply(seedPrev).mod(bbsGenerator.getBigIntegerN());
                outputBitStringBuilder.append(seedNext.mod(BigInteger.valueOf(2)));
                seedPrev = seedNext;
            }
            threadResult = outputBitStringBuilder.toString();
        } else if (threadOption == 2) {
            for (int index = 0; index < lengthToGenerate; ++index) {
                BigInteger seedCurr = jacobiGenerator.getSeed0().add(BigInteger.valueOf(index));
                int jacobiCalc = JacobiGenerator.calcJacobiSymbol(seedCurr, jacobiGenerator.getBigIntegerN());
                if (jacobiCalc == -1)
                    jacobiCalc++;
                outputBitStringBuilder.append(jacobiCalc);
            }
            threadResult = outputBitStringBuilder.toString();
        }

        return threadResult;
    }
}
