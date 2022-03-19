package vigenerecrypting;

import java.util.HashMap;
import java.util.Map;

// a class of english constants
public class EnglishConstants {
    static public final int NO_LETTERS = 26;
    static public final Map<Character, Double> LETTER_FREQUENCIES = new HashMap<>();

    static {
        LETTER_FREQUENCIES.put('A', 8.4966);
        LETTER_FREQUENCIES.put('B', 2.0720);
        LETTER_FREQUENCIES.put('C', 4.5388);
        LETTER_FREQUENCIES.put('D', 3.3844);
        LETTER_FREQUENCIES.put('E', 11.1607);
        LETTER_FREQUENCIES.put('F', 1.8121);
        LETTER_FREQUENCIES.put('G', 2.4705);
        LETTER_FREQUENCIES.put('H', 3.0034);
        LETTER_FREQUENCIES.put('I', 7.5448);
        LETTER_FREQUENCIES.put('J', 0.1965);
        LETTER_FREQUENCIES.put('K', 1.1016);
        LETTER_FREQUENCIES.put('L', 5.4893);
        LETTER_FREQUENCIES.put('M', 3.0129);
        LETTER_FREQUENCIES.put('N', 6.6544);
        LETTER_FREQUENCIES.put('O', 7.1635);
        LETTER_FREQUENCIES.put('P', 3.1671);
        LETTER_FREQUENCIES.put('Q', 0.1962);
        LETTER_FREQUENCIES.put('R', 7.5809);
        LETTER_FREQUENCIES.put('S', 5.7351);
        LETTER_FREQUENCIES.put('T', 6.9509);
        LETTER_FREQUENCIES.put('U', 3.6308);
        LETTER_FREQUENCIES.put('V', 1.0074);
        LETTER_FREQUENCIES.put('W', 1.2899);
        LETTER_FREQUENCIES.put('X', 0.2902);
        LETTER_FREQUENCIES.put('Y', 1.7779);
        LETTER_FREQUENCIES.put('Z', 0.2722);
    }
    // letter frequencies were taken from https://www3.nd.edu/~busiforc/handouts/cryptography/letterfrequencies.html

}
