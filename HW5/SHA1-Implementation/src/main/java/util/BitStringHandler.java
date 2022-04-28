package util;

public class BitStringHandler {
    public static String binaryToHexString(String binaryString) {
        return Integer.toHexString((int) Long.parseLong(binaryString, 2));
    }

    public static String hexToBinaryString(String hexString) {
        return Long.toBinaryString(Long.parseLong(hexString, 16));
    }

    public static String hexToBinaryString(int hexValue) {
        return Integer.toBinaryString(hexValue);
    }

    public static String binaryToDecimalString(String binaryString) {
        return Integer.toString((int) Long.parseLong(binaryString, 2));
    }

    public static String decimalToBinaryString(String decimalString) {
        return Integer.toBinaryString((int) Long.parseLong(decimalString));
    }

    public static long binaryStringToInteger(String binaryString) {
        return Long.parseLong(binaryString, 2);
    }

    public static String integerToBinaryString(int decimalValue) {
        return Integer.toBinaryString(decimalValue);
    }

    private static boolean isBitStringValid(String bitString) {
        if (bitString == null)
            return false;

        for (int index = 0; index < bitString.length(); ++index) {
            if (bitString.charAt(index) != '0' && bitString.charAt(index) != '1') {
                return false;
            }
        }
        return true;
    }

    public static String notBitString(String bitString) {
        if (!isBitStringValid(bitString)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }

        StringBuilder notResult = new StringBuilder();
        for (int index = 0; index < bitString.length(); ++index) {
            if (bitString.charAt(index) == '0') {
                notResult.append('1');
            }
            else {
                notResult.append('0');
            }
        }

        return notResult.toString();
    }

    public static String xorBitStrings(String bitString1, String bitString2) {
        if (bitString1.length() != bitString2.length()) {
            throw new IllegalArgumentException("BitStrings must have the same length in order to XOR them!");
        }
        if (!isBitStringValid(bitString1) || !isBitStringValid(bitString2)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }

        StringBuilder xorResult = new StringBuilder();
        for (int index = 0; index < bitString1.length(); ++index) {
            if (bitString1.charAt(index) == bitString2.charAt(index)) {
                xorResult.append('0');
            }
            else {
                xorResult.append('1');
            }
        }

        return xorResult.toString();
    }

    public static String andBitStrings(String bitString1, String bitString2) {
        if (bitString1.length() != bitString2.length()) {
            throw new IllegalArgumentException("BitStrings must have the same length in order to AND them!");
        }
        if (!isBitStringValid(bitString1) || !isBitStringValid(bitString2)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }

        StringBuilder andResult = new StringBuilder();
        for (int index = 0; index < bitString1.length(); ++index) {
            if (bitString1.charAt(index) == '1' && bitString2.charAt(index) == '1') {
                andResult.append('1');
            }
            else {
                andResult.append('0');
            }
        }

        return andResult.toString();
    }

    public static String orBitStrings(String bitString1, String bitString2) {
        if (bitString1.length() != bitString2.length()) {
            throw new IllegalArgumentException("BitStrings must have the same length in order to AND them!");
        }
        if (!isBitStringValid(bitString1) || !isBitStringValid(bitString2)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }

        StringBuilder orResult = new StringBuilder();
        for (int index = 0; index < bitString1.length(); ++index) {
            if (bitString1.charAt(index) == '1' || bitString2.charAt(index) == '1') {
                orResult.append('1');
            }
            else {
                orResult.append('0');
            }
        }

        return orResult.toString();
    }

    public static String padRightBitStringWithZeros(String bitString, int requiredSize) {
        if (!isBitStringValid(bitString)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }
        if (requiredSize < bitString.length()) {
            throw new IllegalArgumentException("Required size must be greater than or equal to the current bitString size!");
        }

        int neededBitsCount = requiredSize - bitString.length();
        return "0".repeat(neededBitsCount) +
                bitString;
    }

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

    public static int calculateHammingDistance(String bitString1, String bitString2) {
        if (bitString1.length() != bitString2.length()) {
            throw new IllegalArgumentException("BitStrings must have the same length in order to AND them!");
        }
        if (!isBitStringValid(bitString1) || !isBitStringValid(bitString2)) {
            throw new IllegalArgumentException("Invalid bitString given as parameter!");
        }

        int counter = 0;
        for (int index = 0; index < bitString1.length(); ++index) {
            if (bitString1.charAt(index) != bitString2.charAt(index)) {
                counter++;
            }
        }
        return counter;
    }
}
