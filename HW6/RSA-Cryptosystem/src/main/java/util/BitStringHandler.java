package util;

/** Util class for working with bitStrings. */
public abstract class BitStringHandler {
    public static String convertMsgToBitString(String str) {
        StringBuilder bitStringBuilder = new StringBuilder();
        for (int charIndex = 0; charIndex < str.length(); ++charIndex) {
            String charBitString = Integer.toBinaryString(str.charAt(charIndex));
            charBitString = "0".repeat(Math.max(0, 8 - charBitString.length())) +
                    charBitString;

            bitStringBuilder.append(charBitString);
        }
        return bitStringBuilder.toString();
    }

    public static String convertBitStringToMsg(String bitString) {
        StringBuilder resultStringBuilder = new StringBuilder();
        for (int byteIndex = 0; byteIndex < bitString.length(); byteIndex += 8) {
            String chBitString = bitString.substring(byteIndex, byteIndex + 8);
            char ch = ((char) Integer.parseInt(chBitString, 2));
            resultStringBuilder.append(ch);
        }
        return resultStringBuilder.toString();
    }
}