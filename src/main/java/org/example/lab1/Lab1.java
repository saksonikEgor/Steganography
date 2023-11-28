package org.example.lab1;

import org.example.utils.BinaryUtils;
import org.example.utils.FileUtils;

import java.nio.file.Path;

public class Lab1 {
    private static final char VOID_SPACE_CHARACTER = 'ㅤ';
    private static final Path TEXT_PATH = Path.of("src/main/resources/lab1/text.txt");
    private static final Path TEXT_WITH_MESSAGE_PATH = Path.of("src/main/resources/lab1/textWithMessage.txt");

    private Lab1() {
    }

    public static void encode(String message) {
        String binaryMessage = BinaryUtils.textToBinaryString(message);
        StringBuilder sb = new StringBuilder(FileUtils.readTextFromTheFile(TEXT_PATH));
        int idx = binaryMessage.length() - 1;

        for (int i = 0; i < sb.length(); i++) {
            if (idx == -1) {
                break;
            }

            if (Character.isSpaceChar(sb.charAt(i))) {
                if (binaryMessage.charAt(idx) == '1') {
                    sb.setCharAt(i, VOID_SPACE_CHARACTER);
                }
                idx--;
            }
        }

        FileUtils.writeTextToTheFile(TEXT_WITH_MESSAGE_PATH, sb.toString());
    }

    public static String decode() {
        String textWithMessage = FileUtils.readTextFromTheFile(TEXT_WITH_MESSAGE_PATH);
        StringBuilder sb = new StringBuilder();

        for (int i = textWithMessage.lastIndexOf(VOID_SPACE_CHARACTER) + 1; i >= 0; i--) {
            char c = textWithMessage.charAt(i);

            if (c == VOID_SPACE_CHARACTER) {
                sb.append('1');
            } else if (Character.isSpaceChar(c)) {
                sb.append('0');
            }
        }

        System.out.println(sb);
        return BinaryUtils.binaryStringToText(sb.toString());
    }

    public static void main(String[] args) {
        encode("secret password is 'PASSWORD'");

        System.out.println(decode());
    }
}
