import generators.*;
import utilities.RunSettings;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        if (args.length < 1) {
            System.out.println("Syntax: java Main <bbs/jac> [<no_of_iterations> -- default: 10^6]");
        }

        if (args.length == 2) {
            RunSettings.MAX_ITERATIONS = Integer.parseInt(args[1]);
        }

        if (args[0].equals("bbs")) {
            BBSGenerator bbsGenerator = new BBSGenerator();
            bbsGenerator.calculateOutput();
            System.out.println("BBSGenerator:");
            // System.out.println(bbsGenerator.getOutputBitString());
            bbsGenerator.writeNumberToDefaultFile();
        } else if (args[0].equals("jac") || args[0].equals("jacobi")) {
            JacobiGenerator jacobiGenerator = new JacobiGenerator();
            jacobiGenerator.calculateOutput();
            System.out.println("JacobiGenerator:");
            // System.out.println(jacobiGenerator.getOutputBitString());
            jacobiGenerator.writeNumberToDefaultFile();
        }

        long endTime = System.nanoTime();
        System.out.println("Time taken: " + ((double) (endTime - startTime) / 1000000000.0) + " seconds");
    }
}
