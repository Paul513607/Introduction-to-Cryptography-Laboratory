package generators;

public interface Generator {
    void setupGenerator();
    void calculateOutput();
    String getOutputBitString();
    void writeNumberToDefaultFile();
}
