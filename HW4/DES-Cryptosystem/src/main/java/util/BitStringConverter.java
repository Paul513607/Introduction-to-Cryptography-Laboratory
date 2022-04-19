package util;

public class BitStringConverter {
    public static String convertMsgToBitString(String blockString) {
        StringBuilder blockBuilder = new StringBuilder();
        for (int charIndex = 0; charIndex < blockString.length(); ++charIndex) {
            String charBitString = Integer.toBinaryString(blockString.charAt(charIndex));
            StringBuilder charBitStringBuilder  = new StringBuilder();
            for (int fill = 0; fill < 8 - charBitString.length(); ++fill) {
                charBitStringBuilder.append('0');
            }
            charBitStringBuilder.append(charBitString);
            charBitString = charBitStringBuilder.toString();

            blockBuilder.append(charBitString);
        }
        return blockBuilder.toString();
    }

    public static String convertBitStringToMsg(String blockBitString) {
        StringBuilder blockMsgBuilder = new StringBuilder();
        for (int byteIndex = 0; byteIndex < blockBitString.length(); byteIndex += 8) {
            String chBitString = blockBitString.substring(byteIndex, byteIndex + 8);
            char ch = ((char) Integer.parseInt(chBitString, 2));
            blockMsgBuilder.append(ch);
        }
        return blockMsgBuilder.toString();
    }
}
