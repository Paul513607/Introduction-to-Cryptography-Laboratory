import generators.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java Main <bbs/jac>");
        }

        if (args[0].equals("bbs")) {
            BBSGenerator bbsGenerator = new BBSGenerator();
            bbsGenerator.calculateOutput();
            System.out.println("BBSGenerator:");
            System.out.println(bbsGenerator.getOutputBitString());
            bbsGenerator.writeNumberToDefaultFile();
        } else if (args[0].equals("jac") || args[0].equals("jacobi")) {
            JacobiGenerator jacobiGenerator = new JacobiGenerator();
            jacobiGenerator.calculateOutput();
            System.out.println("JacobiGenerator:");
            System.out.println(jacobiGenerator.getOutputBitString());
            jacobiGenerator.writeNumberToDefaultFile();
        }
    }
}
