package com.HiWord9;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ColorRounder {
    public static void main(String[] args) throws IOException {
        System.out.println("Made by github.com/HiWord9 on Earth");
        while (true) {
            program();
        }
    }

    static void program() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inter Full Path to source Image");
        String sourcePath = scanner.nextLine();
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.err.println("Incorrect Path to image");
        } else {
            BufferedImage sourceImage = ImageIO.read(sourceFile);

            System.out.println("Inter Roundness");
            String inputRoundness = scanner.nextLine();
            while (!inputRoundness.matches("[0-9]+")) {
                System.out.println("Incorrect input");
                inputRoundness = scanner.nextLine();
            }
            int roundness = Integer.parseInt(inputRoundness);

            int width = 0;
            int height = 0;
            System.out.println("Inter result Width");
            while (true) {
                String inputWidth = scanner.nextLine();
                if (inputWidth.matches("[0-9]*")) {
                    if (!inputWidth.equals("")) {
                        width = Integer.parseInt(inputWidth);
                    }
                    break;
                }
                System.err.println("Incorrect Width");
            }
            System.out.println("Inter result Height");
            while (true) {
                String inputHeight = scanner.nextLine();
                if (inputHeight.matches("[0-9]*")) {
                    if (!inputHeight.equals("")) {
                        height = Integer.parseInt(inputHeight);
                    }
                    break;
                }
                System.err.println("Incorrect Height");
            }

            System.out.println("Inter Full Output Path");
            String outputPath = scanner.nextLine();
            if (outputPath.equals("")) {
                outputPath = sourcePath.substring(0, sourcePath.lastIndexOf("\\"));
            }

            BufferedImage resultImage;
            if (width == 0 && height == 0) {
                resultImage = roundPixels(sourceImage, roundness);
            } else if (width == 0) {
                resultImage = roundPixels(sourceImage, sourceImage.getWidth(), height, roundness);
            } else if (height == 0) {
                resultImage = roundPixels(sourceImage, width, sourceImage.getHeight(), roundness);
            } else {
                resultImage = roundPixels(sourceImage, width, height, roundness);
            }

            File resultFile = null;
            int attempts = 0;
            while (resultFile == null || resultFile.exists()) {
                String resultFileName = outputPath.endsWith(".png") ? "" :
                        (sourceFile.toPath().getFileName().toString().substring(0, sourceFile.toPath().getFileName().toString().lastIndexOf(".")) +
                                "_ROUNDED+" + roundness + (attempts == 0 ? "" : "_" + attempts) + ".png");
                resultFile = new File(outputPath, resultFileName);
                attempts++;
            }

            resultFile.mkdirs();
            ImageIO.write(resultImage, "png", resultFile);
            System.out.println("Successfully saved Result Image in " + resultFile.toPath());
            scanner.nextLine();
        }
    }

    static BufferedImage roundPixels(BufferedImage sourceImage, int roundness) {
        return roundPixels(sourceImage, sourceImage.getWidth(), sourceImage.getHeight(), roundness);
    }

    static BufferedImage roundPixels(BufferedImage sourceImage, int width, int height, int roundness) {
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ColorModel colorModel = sourceImage.getColorModel();
        WritableRaster writableRaster = sourceImage.getRaster();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xOnSource = (x * (sourceImage.getWidth() / width));
                int yOnSource = (y * (sourceImage.getHeight() / height));
                var dataElements = writableRaster.getDataElements(xOnSource, yOnSource, null);
                resultImage.setRGB(x, y,
                        roundRGB(colorModel.getRed(dataElements),
                                colorModel.getGreen(dataElements),
                                colorModel.getBlue(dataElements),
                                roundness)
                );
            }
        }
        return resultImage;
    }

    static int roundRGB(int r, int g, int b, int roundness) {
        int divRoundness = roundness / 2;
        return ((Math.min(((r + divRoundness) / roundness) * roundness, 255)<< 16)
                | (Math.min(((g + divRoundness) / roundness) * roundness, 255) << 8)
                | (Math.min(((b + divRoundness) / roundness) * roundness, 255)));
    }
}
