package org.example.lab3;

import org.example.model.Component;
import org.example.utils.BinaryUtils;
import org.example.utils.FileUtils;
import org.example.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.IntStream;

public class Lab3 {
    private static final Path IMAGE_PATH = Path.of("src/main/resources/lab3/sourceImage.bmp");
    private static final Path IMAGE_WITH_TEXT_PATH = Path.of("src/main/resources/lab3/imageWithText.bmp");
    private static final int ALPHA = 5;
    private static final int BLOCK_SIZE = 8;

    private Lab3() {
    }

    public static int encode(String message) {
        BufferedImage image = FileUtils.readImage(IMAGE_PATH);
        int containerCapacity = getContainerCapacity(image);

        String bits = BinaryUtils.textToBinaryString(message);
        System.out.println(bits);

        int bitIndex = 0;
        for (int i = 0; i < image.getHeight(); i += 8) {
            for (int j = 0; j < image.getWidth(); j += 8) {
                if (bitIndex < bits.length() && bitIndex < containerCapacity) {
                    BufferedImage subImage = image.getSubimage(j, i, BLOCK_SIZE, BLOCK_SIZE);

                    int[][] blue = ImageUtils.getComponents(subImage, Component.BLUE);

                    int[][] dct = ImageUtils.directDiscreteCosineTransform(blue);
                    int indexOfMax = getMaxIndexInMatrix(dct);

                    dct[indexOfMax / image.getWidth()][indexOfMax % image.getWidth()]
                            = dct[indexOfMax / image.getWidth()][indexOfMax % image.getWidth()]
                            + ALPHA * bits.charAt(bitIndex) == 0 ? 1 : -1;


                    ImageUtils.setSubImage(
                            image,
                            ImageUtils.combineComponents(
                                    ImageUtils.getComponents(subImage, Component.RED),
                                    ImageUtils.getComponents(subImage, Component.GREEN),
                                    ImageUtils.inverseDiscreteCosineTransform(dct)
                            ),
                            j,
                            i
                    );

                    bitIndex++;
                }
            }
        }

        FileUtils.writeImage(IMAGE_WITH_TEXT_PATH, image);
        return bitIndex;
    }

    public static String decode(int messageSize) {
        BufferedImage sourceImage = FileUtils.readImage(IMAGE_PATH);
        BufferedImage processed = FileUtils.readImage(IMAGE_WITH_TEXT_PATH);

        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < sourceImage.getHeight(); i += 8) {
            for (int j = 0; j < sourceImage.getWidth(); j += 8) {
                if (bits.length() < messageSize) {
                    int[][] dctSBlue = ImageUtils.directDiscreteCosineTransform(
                            ImageUtils.getComponents(
                                    sourceImage.getSubimage(j, i, BLOCK_SIZE, BLOCK_SIZE),
                                    Component.BLUE
                            )
                    );
                    int[][] dctPBlue = ImageUtils.directDiscreteCosineTransform(
                            ImageUtils.getComponents(
                                    processed.getSubimage(j, i, BLOCK_SIZE, BLOCK_SIZE),
                                    Component.BLUE
                            )
                    );

                    int indexOfMax = getMaxIndexInMatrix(dctSBlue);

                    int y = indexOfMax / dctSBlue[0].length;
                    int x = indexOfMax % dctSBlue[0].length;

                    bits.append(dctPBlue[y][x] > dctSBlue[y][x] ? '0' : '1');
                }
            }
        }

        System.out.println(bits);
        return BinaryUtils.binaryStringToText(bits.toString());
    }

    private static int getContainerCapacity(BufferedImage image) {
        return (image.getWidth() / 8) * (image.getHeight() / 8);
    }

//    public BufferedImage insertMessage(String message, double alpha) {
//        System.out.println("INSERTION");
//        int capacity = getContainerCapacity();
//        System.out.println("Container capacity = " + capacity + " bits");
//        int n = 8;
//        int[] bits = Utils.getBitArray(message);
//        int bitIndex = 0;
//        BufferedImage stegoImage = Utils.copyImage(image);
//        for (int i = 0; i < image.getHeight(); i += 8) {
//            for (int j = 0; j < image.getWidth(); j += 8) {
//                if (bitIndex < bits.length && bitIndex < capacity) {
//                    BufferedImage subImage = image.getSubimage(j, i, n, n);
//                    int[][] red = Utils.getComponent(subImage, "red");
//                    int[][] green = Utils.getComponent(subImage, "green");
//                    int[][] blue = Utils.getComponent(subImage, "blue");
//                    int[][] dct = Utils.directDiscreteCosineTransform(red);
//                    int[] index = Utils.getIndexOfMax(dct);
//                    int s = bits[bitIndex] == 0 ? 1 : -1;
//                    dct[index[0]][index[1]] = (int) (dct[index[0]][index[1]] + alpha * s);
//                    int[][] idctRed = Utils.inverseDiscreteCosineTransform(dct);
//                    int[][] rgb = Utils.combineComponents(idctRed, green, blue);
//                    Utils.setSubImage(stegoImage, rgb, j, i);
//                    bitIndex++;
//                }
//            }
//        }
//        System.out.println("Embedded " + bitIndex + " bits");
//        try {
//            Files.write(Path.of("src/main/resources/size.txt"), String.valueOf(bitIndex).getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return stegoImage;
//    }
//
//    public int[] extractMessage(BufferedImage processed) {
//        System.out.println("EXTRACTION");
//        int n = 8;
//        int messageLength = 0;
//        try {
//            messageLength = Integer.parseInt(Files.readString(Path.of("src/main/resources/size.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int index = 0;
//        int[] bits = new int[messageLength];
//        for (int i = 0; i < image.getHeight(); i += 8) {
//            for (int j = 0; j < image.getWidth(); j += 8) {
//                if (index < messageLength) {
//                    BufferedImage oSubImage = image.getSubimage(j, i, n, n);
//                    BufferedImage pSubImage = processed.getSubimage(j, i, n, n);
//                    int[][] oRed = Utils.getComponent(oSubImage, "red");
//                    int[][] pRed = Utils.getComponent(pSubImage, "red");
//                    int[][] dctOriginalRed = Utils.directDiscreteCosineTransform(oRed);
//                    int[][] dctProcessedRed = Utils.directDiscreteCosineTransform(pRed);
//                    int[] indexOfMax = Utils.getIndexOfMax(dctOriginalRed);
//                    int y = indexOfMax[0];
//                    int x = indexOfMax[1];
//                    bits[index] = dctProcessedRed[y][x] > dctOriginalRed[y][x] ? 0 : 1;
//                    index++;
//                }
//            }
//        }
//        return bits;
//    }

    private static int getMaxIndexInMatrix(int[][] matrix) {
        return IntStream.range(0, matrix.length * matrix[0].length)
                .boxed()
                .max(Comparator.comparingInt(idx -> matrix[idx / matrix[0].length][idx % matrix[0].length]))
                .orElseThrow(() -> new RuntimeException("No pixels in image"));
    }

    public static void main(String[] args) {
        System.out.println(decode(encode("se")));
    }
}
