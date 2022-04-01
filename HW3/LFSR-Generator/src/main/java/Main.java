import generators.*;
import utilities.*;

public class Main {
    public static void main(String[] args) {
        int registerSize = 16;
        if (args.length >= 1) {
            RunSettings.MAX_ITERATIONS = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            registerSize = Integer.parseInt(args[1]);
        }

        // start the timer
        Timer timer = Timer.getInstance();
        timer.start();

        LFSRGenerator lfsrGenerator = new LFSRGenerator(registerSize); // default 16
        lfsrGenerator.generateOutputBitString();
        lfsrGenerator.writeNumberToDefaultFile();
        // lfsrGenerator.printNumber();

        // end the timer
        timer.stop();
        timer.showTimeTaken();
    }
}
