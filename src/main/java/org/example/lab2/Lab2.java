package org.example.lab2;

import org.example.utils.BinaryUtils;
import org.example.utils.FileUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class Lab2 {
    private static final Path IMAGE_PATH = Path.of("src/main/resources/lab2/sourceImage.bmp");
    private static final Path IMAGE_WITH_TEXT_PATH = Path.of("src/main/resources/lab2/imageWithText.bmp");
    private static final int LENGTH_CONTAINER_SIZE = 13;

    private Lab2() {
    }

    public static void encode(String message) {
        BufferedImage image = FileUtils.readImage(IMAGE_PATH);

        String binaryMessage = BinaryUtils.textToBinaryString(message);
        binaryMessage = addLeadingZerosToLength(binaryMessage, binaryMessage.length() + 6 - (binaryMessage.length() % 6));

        String binaryLengthString = addLeadingZerosToLength(
                Integer.toBinaryString(binaryMessage.length()),
                LENGTH_CONTAINER_SIZE * 6
        );

        int idx = 0;
        for (int i = 0; i < LENGTH_CONTAINER_SIZE; i++) {
            Color color = new Color(image.getRGB(0, i));
            image.setRGB(
                    0, i, new Color(
                            color.getRed() - (color.getRed() % 4) + calculateLSBValueByString(binaryLengthString, idx),
                            color.getGreen() - (color.getGreen() % 4) + calculateLSBValueByString(binaryLengthString, idx + 2),
                            color.getBlue() - (color.getBlue() % 4) + calculateLSBValueByString(binaryLengthString, idx + 4)
                    ).getRGB()
            );
            idx += 6;
        }

        idx = 0;
        outerLoop:
        for (int i = 1; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (idx == binaryMessage.length()) {
                    break outerLoop;
                }

                Color color = new Color(image.getRGB(i, j));
                image.setRGB(
                        i, j, new Color(
                                color.getRed() - (color.getRed() % 4) + calculateLSBValueByString(binaryMessage, idx),
                                color.getGreen() - (color.getGreen() % 4) + calculateLSBValueByString(binaryMessage, idx + 2),
                                color.getBlue() - (color.getBlue() % 4) + calculateLSBValueByString(binaryMessage, idx + 4)
                        ).getRGB()
                );
                idx += 6;
            }
        }

        FileUtils.writeImage(IMAGE_WITH_TEXT_PATH, image);
    }

    public static String decode() {
        BufferedImage image = FileUtils.readImage(IMAGE_WITH_TEXT_PATH);
        StringBuilder lengthSb = new StringBuilder();

        for (int i = 0; i < LENGTH_CONTAINER_SIZE; i++) {
            Color color = new Color(image.getRGB(0, i));

            lengthSb.append(addLeadingZerosToLength(Integer.toBinaryString(color.getRed() % 4), 2));
            lengthSb.append(addLeadingZerosToLength(Integer.toBinaryString(color.getGreen() % 4), 2));
            lengthSb.append(addLeadingZerosToLength(Integer.toBinaryString(color.getBlue() % 4), 2));
        }

        int messageLength = Integer.parseInt(lengthSb.toString(), 2);

        StringBuilder binaryMessage = new StringBuilder();
        outerLoop:
        for (int i = 1; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (binaryMessage.length() >= messageLength) {
                    break outerLoop;
                }

                Color color = new Color(image.getRGB(i, j));
                binaryMessage.append(addLeadingZerosToLength(Integer.toBinaryString(color.getRed() % 4), 2));
                binaryMessage.append(addLeadingZerosToLength(Integer.toBinaryString(color.getGreen() % 4), 2));
                binaryMessage.append(addLeadingZerosToLength(Integer.toBinaryString(color.getBlue() % 4), 2));
            }
        }

        return BinaryUtils.binaryStringToText(binaryMessage.toString());
    }

    private static String addLeadingZerosToLength(String str, int length) {
        StringBuilder sb = new StringBuilder(str).reverse();
        while (sb.length() < length) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }

    private static int calculateLSBValueByString(String str, int idx) {
        int result = 0;

        if (str.charAt(idx) == '1') {
            result += 2;
        }
        if (str.charAt(idx + 1) == '1') {
            result += 1;
        }

        return result;
    }

    public static void main(String[] args) {
        encode("secret password is = 'PASSWORD'");

        System.out.println(decode());
    }
}
