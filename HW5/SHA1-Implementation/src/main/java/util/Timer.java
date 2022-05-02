package util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Timer {
    private static Timer timerInstance = null;
    private long startTime;
    private long endTime;
    private double duration;

    public static Timer getInstance() {
        if (timerInstance == null)
            timerInstance = new Timer();
        return timerInstance;
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
        duration = (double) (endTime - startTime) / 1000000000.0;
    }

    public void showTimeTaken() {
        System.out.println("Time taken: " + duration + "s (seconds).");
    }
}