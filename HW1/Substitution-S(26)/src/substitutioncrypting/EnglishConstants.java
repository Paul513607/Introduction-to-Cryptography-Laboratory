package substitutioncrypting;

import java.util.HashMap;
import java.util.Map;

// a class of english constants
public class EnglishConstants {
    static public final int NO_LETTERS = 26;
    static public final Map<Character, Double> LETTER_FREQUENCIES = new HashMap<>();
    static public final Map<String, Double> BIGRAM_FREQUENCIES = new HashMap<>();
    static public final Map<String, Double> TRIGRAM_FREQUENCIES = new HashMap<>();

    static {
        LETTER_FREQUENCIES.put('A', 8.000395);
        LETTER_FREQUENCIES.put('B', 1.535701);
        LETTER_FREQUENCIES.put('C', 2.575785);
        LETTER_FREQUENCIES.put('D', 4.317924);
        LETTER_FREQUENCIES.put('E', 12.575645);
        LETTER_FREQUENCIES.put('F', 2.350463);
        LETTER_FREQUENCIES.put('G', 1.982677);
        LETTER_FREQUENCIES.put('H', 6.236609);
        LETTER_FREQUENCIES.put('I', 6.920007);
        LETTER_FREQUENCIES.put('J', 0.145188);
        LETTER_FREQUENCIES.put('K', 0.739906);
        LETTER_FREQUENCIES.put('L', 4.057231);
        LETTER_FREQUENCIES.put('M', 2.560994);
        LETTER_FREQUENCIES.put('N', 6.903785);
        LETTER_FREQUENCIES.put('O', 7.591270);
        LETTER_FREQUENCIES.put('P', 1.795742);
        LETTER_FREQUENCIES.put('Q', 0.117571);
        LETTER_FREQUENCIES.put('R', 5.959034);
        LETTER_FREQUENCIES.put('S', 6.340880);
        LETTER_FREQUENCIES.put('T', 9.085226);
        LETTER_FREQUENCIES.put('U', 2.841783);
        LETTER_FREQUENCIES.put('V', 0.981717);
        LETTER_FREQUENCIES.put('W', 2.224893);
        LETTER_FREQUENCIES.put('X', 0.179556);
        LETTER_FREQUENCIES.put('Y', 1.900888);
        LETTER_FREQUENCIES.put('Z', 0.079130);
    }

    static {
        BIGRAM_FREQUENCIES.put("th", 3.882543);
        BIGRAM_FREQUENCIES.put("he", 3.681391);
        BIGRAM_FREQUENCIES.put("in", 2.283899);
        BIGRAM_FREQUENCIES.put("er", 2.178042);
        BIGRAM_FREQUENCIES.put("an", 2.140460);
        BIGRAM_FREQUENCIES.put("re", 1.749394);
        BIGRAM_FREQUENCIES.put("nd", 1.571977);
        BIGRAM_FREQUENCIES.put("on", 1.418244);
        BIGRAM_FREQUENCIES.put("en", 1.383239);
        BIGRAM_FREQUENCIES.put("at", 1.335523);
        BIGRAM_FREQUENCIES.put("ou", 1.285484);
        BIGRAM_FREQUENCIES.put("ed", 1.275779);
        BIGRAM_FREQUENCIES.put("ha", 1.274742);
        BIGRAM_FREQUENCIES.put("to", 1.169655);
        BIGRAM_FREQUENCIES.put("or", 1.151094);
        BIGRAM_FREQUENCIES.put("it", 1.134891);
        BIGRAM_FREQUENCIES.put("is", 1.109877);
        BIGRAM_FREQUENCIES.put("hi", 1.092302);
        BIGRAM_FREQUENCIES.put("es", 1.092301);
        BIGRAM_FREQUENCIES.put("ng", 1.053385);
    }

    static {
        TRIGRAM_FREQUENCIES.put("the", 3.508232);
        TRIGRAM_FREQUENCIES.put("and", 1.593878);
        TRIGRAM_FREQUENCIES.put("ing", 1.147042);
        TRIGRAM_FREQUENCIES.put("her", 0.822444);
        TRIGRAM_FREQUENCIES.put("hat", 0.650715);
        TRIGRAM_FREQUENCIES.put("his", 0.596748);
        TRIGRAM_FREQUENCIES.put("tha", 0.593593);
        TRIGRAM_FREQUENCIES.put("ere", 0.560594);
        TRIGRAM_FREQUENCIES.put("for", 0.555372);
        TRIGRAM_FREQUENCIES.put("ent", 0.530771);
        TRIGRAM_FREQUENCIES.put("ion", 0.506454);
        TRIGRAM_FREQUENCIES.put("ter", 0.461099);
        TRIGRAM_FREQUENCIES.put("was", 0.460487);
        TRIGRAM_FREQUENCIES.put("you", 0.437213);
        TRIGRAM_FREQUENCIES.put("ith", 0.431250);
        TRIGRAM_FREQUENCIES.put("ver", 0.430732);
        TRIGRAM_FREQUENCIES.put("all", 0.422758);
        TRIGRAM_FREQUENCIES.put("wit", 0.397290);
        TRIGRAM_FREQUENCIES.put("thi", 0.394796);
        TRIGRAM_FREQUENCIES.put("tio", 0.378058);
    }
    // letter frequencies were taken from https://www3.nd.edu/~busiforc/handouts/cryptography/Letter%20Frequencies.html

}
