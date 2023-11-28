package org.example.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class FileUtils {
    private FileUtils() {
    }

    public static String readTextFromTheFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Cant read file", e);
        }
    }

    public static void writeTextToTheFile(Path path, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cant write to the file", e);
        }
    }

    public static BufferedImage readImage(Path path) {
        try {
            return ImageIO.read(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Cant read image", e);
        }
    }

    public static void writeImage(Path path, BufferedImage image) {
        try {
            ImageIO.write(image, "bmp", path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Cant write image", e);
        }
    }
}
