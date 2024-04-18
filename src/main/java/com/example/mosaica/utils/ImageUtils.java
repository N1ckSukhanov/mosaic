package com.example.mosaica.utils;


import com.example.mosaica.entity.ImageBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ImageUtils {

    public static final List<Color> brownPalette = List.of(
            Color.decode("#1e1108"),
            Color.decode("#492a13"),
            Color.decode("#7a451f"),
            Color.decode("#94631a"),
            Color.decode("#985e33"),
            Color.decode("#b87748"),
            Color.decode("#cb9051"),
            Color.decode("#e4bb8e"),
            Color.decode("#f5eccb"),
            Color.decode("#fffbef")
    );

    public static final List<Color> grayPalette = List.of(
            Color.decode("#000000"),
            Color.decode("#1b2853"),
            Color.decode("#424242"),
            Color.decode("#6c6c6c"),
            Color.decode("#8c8c8c"),
            Color.decode("#ababab"),
            Color.decode("#848484"),
            Color.decode("#d3d3d6"),
            Color.decode("#ececec"),
            Color.decode("#f9f7f1")
    );
    public static final int MOSAIC_SIZE = 10;

    public static BlockResult convertToMosaica(InputStream originalStream, String size, String palette, Path convertedPath) throws IOException {
        var originalImage = ImageIO.read(originalStream);
        var convertedImage = createMosaic(originalImage, size, palette);
        var result = getBlocksForMosaic(convertedImage, palette, size);

        ImageIO.write(convertedImage, "png", convertedPath.toFile());
        return result;
    }

    private static BlockResult getBlocksForMosaic(BufferedImage mosaic, String palette, String size) {
        List<Color> colors = getColorsForPalette(palette);
        int blockWidth, blockHeight;
        switch (size) {
            case "a4" -> {
                blockWidth = 10;
                blockHeight = 12;
            }
            case "a3" -> {
                blockWidth = 12;
                blockHeight = 12;
            }
            case "40x50" -> {
                blockWidth = 10;
                blockHeight = 10;
            }

            default -> throw new IllegalArgumentException("Invalid Image size");
        }

        if (mosaic.getWidth() <= mosaic.getHeight()) {
            int t = blockHeight;
            blockHeight = blockWidth;
            blockWidth = t;
        }

        int horizontalBlocks = mosaic.getWidth() / MOSAIC_SIZE / blockWidth;
        int verticalBlocks = mosaic.getHeight() / MOSAIC_SIZE / blockHeight;

        int blockNumber = 1;
        List<ImageBlock> blocks = new ArrayList<>(horizontalBlocks * verticalBlocks);

        for (int x = 0; x < horizontalBlocks; x++) {
            for (int y = 0; y < verticalBlocks; y++) {
                ImageBlock block = ImageBlock.builder()
                        .blockNumber(blockNumber)
                        .blockColors(getColorsForBlock(mosaic, blockWidth, blockHeight, x, y, colors))
                        .width(blockWidth)
                        .height(blockHeight)
                        .build();
                blocks.add(block);
                blockNumber++;
            }
        }

        return new BlockResult(
                blocks,
                horizontalBlocks,
                verticalBlocks
        );
    }

    public record BlockResult(List<ImageBlock> blocks, int horizontalBlocks, int verticalBlocks) {

    }

    private static List<Integer> getColorsForBlock(BufferedImage image, int blockWidth, int blockHeight, int x, int y, List<Color> palette) {
        List<Integer> colors = new ArrayList<>(blockHeight * blockWidth);
        int xStart = x * blockWidth * MOSAIC_SIZE;
        int yStart = y * blockHeight * MOSAIC_SIZE;

        for (int xPos = xStart; xPos < xStart + blockWidth * MOSAIC_SIZE; xPos += MOSAIC_SIZE) {
            for (int yPos = yStart; yPos < yStart + blockHeight * MOSAIC_SIZE; yPos += MOSAIC_SIZE) {
                colors.add(getColorForTile(image, palette, xPos, yPos));
            }
        }

        return colors;
    }

    // Return 0 if white
    // Else return number of color in palette, starting from 1
    private static int getColorForTile(BufferedImage image, List<Color> pallete, int x, int y) {
        int tileEnd = MOSAIC_SIZE - 1;
        return Stream
                .of(
                        new Color(image.getRGB(x, y)),
                        new Color(image.getRGB(x + tileEnd, y)),
                        new Color(image.getRGB(x, y + tileEnd)),
                        new Color(image.getRGB(x + tileEnd, y + tileEnd))
                )
                .filter(color -> !color.equals(Color.WHITE))
                .findAny()
                .map(color -> pallete.indexOf(color) + 2)
                .orElse(1);
    }

    private static BufferedImage createMosaic(BufferedImage originalImage, String size, String palette) {
        int desiredWidth, desiredHeight;
        int size1, size2;

        switch (size) {
            case "a4" -> {
                size1 = 210 * 4;
                size2 = 297 * 4;
            }
            case "a3" -> {
                size1 = 297 * 4;
                size2 = 420 * 4;
            }
            case "40x50" -> {
                size1 = 400 * 4;
                size2 = 500 * 4;
            }

            default -> throw new IllegalArgumentException();
        }

        if (originalImage.getWidth() <= originalImage.getHeight()) {
            desiredWidth = size1;
            desiredHeight = size2;
        } else {
            desiredWidth = size2;
            desiredHeight = size1;
        }

        var result = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB);
        var graphics = result.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, desiredWidth, desiredHeight);

        double imageRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        double desiredRatio = (double) desiredWidth / desiredHeight;

        int width;
        int height;

        if (imageRatio > desiredRatio) {
            width = desiredWidth;
            height = (int) (desiredWidth / imageRatio);
        } else {
            height = desiredHeight;
            width = (int) (desiredHeight * imageRatio);
        }

        int x = (desiredWidth - width) / 2;
        int y = (desiredHeight - height) / 2;

        graphics.drawImage(convertToMosaic(scaleImage(originalImage, width, height), palette, MOSAIC_SIZE), x, y, width, height, null);
        graphics.dispose();

        return result;
    }

    private static BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
        var resizedImage = new BufferedImage(width, height, originalImage.getType());
        var graphics = resizedImage.createGraphics();

        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();

        return resizedImage;
    }

    public static BufferedImage convertToMosaic(BufferedImage originalImage, String palette, int mosaicSize) {
        var mosaic = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        var colors = getColorsForPalette(palette);

        for (int y = 0; y < originalImage.getHeight(); y += mosaicSize)
            for (int x = 0; x < originalImage.getWidth(); x += mosaicSize)
                fillRect(mosaic, getAverageColor(originalImage, colors, x, y, mosaicSize), x, y, mosaicSize);

        return mosaic;
    }

    public static List<Color> getColorsForPalette(String palette) {
        return switch (palette) {
            case "brown" -> brownPalette;
            case "gray" -> grayPalette;

            default -> throw new IllegalArgumentException();
        };
    }

    private static Color getAverageColor(BufferedImage image, List<Color> colors, int startX, int startY, int size) {
        float red = 0f, green = 0f, blue = 0f;
        int count = 0;

        for (int y = startY; y < startY + size && y < image.getHeight(); y++) {
            for (int x = startX; x < startX + size && x < image.getWidth(); x++) {
                var pixel = new Color(image.getRGB(x, y));

                red += pixel.getRed();
                green += pixel.getGreen();
                blue += pixel.getBlue();

                count++;
            }
        }

        if (count == 0) throw new IllegalStateException("Count should not be zero");

        return roundToNearestLevel(colors, red / count, green / count, blue / count);
    }

    private static Color roundToNearestLevel(List<Color> colors, float red, float green, float blue) {
        var sorted = colors.stream()
                .map(color -> Tuple2.of(color, getDistance(color, red, green, blue)))
                .sorted(Comparator.comparingDouble(Tuple2::second))
                .toList();

        if (Math.random() < (sorted.get(1).second() - sorted.get(0).second()) / 255 / 255 / 4)
            return sorted.get(1).first();

        if (Math.random() < (sorted.get(2).second() - sorted.get(0).second()) / 255 / 255 / 16)
            return sorted.get(2).first();

        else return sorted.get(0).first();
    }

    private static float getDistance(Color color, float red, float green, float blue) {
        red = Math.abs(color.getRed() - red);
        green = Math.abs(color.getGreen() - green);
        blue = Math.abs(color.getBlue() - blue);

        return red * red + green * green + blue * blue;
    }

    private static void fillRect(BufferedImage image, Color color, int startX, int startY, int size) {
        var graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(startX, startY, size, size);
    }

    public record Tuple2<T1, T2>(T1 first, T2 second) {
        public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
            return new Tuple2<>(first, second);
        }
    }
}
