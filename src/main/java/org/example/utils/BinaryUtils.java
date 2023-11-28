package org.example.utils;

import java.math.BigInteger;

public class BinaryUtils {
    private BinaryUtils() {
    }

    public static String textToBinaryString(String text) {
        return new BigInteger(text.getBytes()).toString(2);
    }

    public static String binaryStringToText(String binary) {
        return new String(new BigInteger(binary, 2).toByteArray());
    }
}
