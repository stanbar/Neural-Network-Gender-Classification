import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Created by stasbar on 22.05.2017.
 */
public class ImageProcessor {
    public static void convertImagesToArrays(File imagesLocation) {
        try {
            if (imagesLocation.listFiles() == null) {
                System.err.println("Could not load files from " + imagesLocation.toString());
                return;
            }

            File subDirFemale = new File(imagesLocation, "Female");
            File subDirMale = new File(imagesLocation, "Male");
            File subDifUndefined = new File(imagesLocation, "Undefined");

            Files.createDirectories(subDirFemale.toPath());
            Files.createDirectories(subDirMale.toPath());
            int filesCounter = 0;
            for (File file : imagesLocation.listFiles()) {
                if (!file.isFile() || file.isHidden())
                    continue;
                int[][] array = convertTo2DWithoutUsingGetRGB(ImageIO.read(file));
                String name = file.getName().toLowerCase();
                File outputDir;
                String postfix = ".txt";
                String fileName = file.getName().substring(0, file.getName().indexOf(".")) + postfix;
                if (name.contains("female") || name.contains("women"))
                    outputDir = subDirFemale;
                else if (name.contains("male") || name.contains("men"))
                    outputDir = subDirMale;
                else
                    outputDir = subDifUndefined;
                writeArrayToFile(array, new File(outputDir, fileName));
                filesCounter++;
            }
            System.out.printf("Wrote %d files\n", filesCounter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeArrayToFile(int[][] array, File outputFile) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        int width = array.length;
        int height = array[0].length;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (w != width - 1)
                    bufferedWriter.write(String.valueOf(array[h][w]) + " ");
                else
                    bufferedWriter.write(String.valueOf(array[h][w]) + "");
            }
            bufferedWriter.write("\n");
            bufferedWriter.flush();
        }

        System.out.printf("Wrote %d x %d numbers from %s \n", height, width, outputFile.getName());


    }


    private static int[][] getPixelsArray(File file) {
        try {
            int[][] pixels;
            BufferedImage img = ImageIO.read(file);
            Raster raster = img.getData();
            int width = raster.getWidth();
            int height = raster.getHeight();
            pixels = new int[width][height];
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    //pixels[w][h] = img.getRGB(w,h);
                    pixels[w][h] = raster.getSample(h, w, 0);
                }

            }
            return pixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Image getImage(int pixels[][]) {
        int width = pixels.length;
        int height = pixels[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int value = pixels[w][h] << 16 | pixels[w][h] << 8 | pixels[w][h];
                image.setRGB(w, h, value);
            }
        }


        saveImage(image);
        return image;
    }

    private static int[][] readPixelsArray(File file) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Scanner scanner = new Scanner(file);
        long count = bufferedReader.lines().count();
        int[][] array = new int[(int) count][];
        int index = 0;
        String line;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] splitedLine = line.split(" ");
            array[index] = new int[splitedLine.length];
            for (int i = 0; i < splitedLine.length; i++) {
                array[index][i] = Integer.parseInt(splitedLine[i]);
            }
            index++;
        }

        System.out.println("Read pixels from array: " + array.length + " x " + array[0].length);
        return array;

    }

    private static void saveImage(BufferedImage image) {

        File output = new File("check.jpg");
        try {
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        ImageProcessor.getImage(readPixelsArray(new File("/Users/admin1/Google Drive/ProjectsJava/NeuralNetworkGenderClassificator/res/Set2/Test/0_1_FEMALE_685_656.txt")));
    }

}
