package org.example.utils;

import org.example.model.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {
    private ImageUtils() {
    }

    public static int[][] getComponents(BufferedImage image, Component component) {
        int[][] components = new int[image.getHeight()][image.getWidth()];

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                components[i][j] = switch (component) {
                    case RED -> new Color(image.getRGB(j, i)).getRed();
                    case GREEN -> new Color(image.getRGB(j, i)).getGreen();
                    case BLUE -> new Color(image.getRGB(j, i)).getBlue();
                };
            }
        }

        return components;
    }

    public static void setSubImage(BufferedImage image, int[][] subImage, int x, int y) {
        for (int i = 0; i < subImage.length; i++) {
            for (int j = 0; j < subImage[0].length; j++) {
                image.setRGB(x + j, y + i, subImage[i][j]);
            }
        }
    }

    public static int[][] directDiscreteCosineTransform(int[][] block) {
        if (block.length != 8 || block[0].length != 8) {
            throw new IllegalArgumentException("Matrix should be 8 x 8 pixels");
        }

        int n = block.length;
        int[][] result = new int[n][n];
        double coeff1, coeff2;
        for (int k = 0; k < n; k++) {
            for (int l = 0; l < n; l++) {
                coeff1 = (k == 0) ? Math.sqrt((double) 1 / n) : Math.sqrt((double) 2 / n);
                coeff2 = (l == 0) ? Math.sqrt((double) 1 / n) : Math.sqrt((double) 2 / n);

                double sum = 0.0;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        sum += block[i][j]
                                * Math.cos(((2 * i + 1) * Math.PI) / (2 * n) * k)
                                * Math.cos(((2 * j + 1) * Math.PI) / (2 * n) * l);
                    }
                }
                result[k][l] = (int) (coeff1 * coeff2 * sum);
            }
        }
        return result;
    }

    public static int[][] inverseDiscreteCosineTransform(int[][] block) {
        if (block.length != 8 || block[0].length != 8) {
            throw new IllegalArgumentException("Matrix should be 8x8 pixels");
        }

        int n = block.length;
        int[][] result = new int[n][n];
        double coeff1, coeff2;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    for (int l = 0; l < n; l++) {
                        coeff1 = (k == 0) ? Math.sqrt((double) 1 / n) : Math.sqrt((double) 2 / n);
                        coeff2 = (l == 0) ? Math.sqrt((double) 1 / n) : Math.sqrt((double) 2 / n);
                        sum += coeff1 * coeff2 * block[k][l]
                                * Math.cos(((2 * i + 1) * Math.PI) / (2 * n) * k)
                                * Math.cos(((2 * j + 1) * Math.PI) / (2 * n) * l);
                    }
                }
                result[i][j] = clip(sum);
            }
        }
        return result;
    }

    private static int clip(double x) {
        if (x < 0) {
            return 0;
        } else if (x > 255) {
            return 255;
        } else {
            return (int) x;
        }
    }

    public static int[][] combineComponents(int[][] red, int[][] green, int[][] blue) {
        int h = red.length;
        int w = red[0].length;

        int[][] result = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                result[i][j] = new Color(red[i][j], green[i][j], blue[i][j]).getRGB();
            }
        }
        return result;
    }

}
