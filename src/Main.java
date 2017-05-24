import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    private File maleFolder = new File("res/Zestaw1/Male");
    private File femaleFolder = new File("res/Zestaw1/Female");
    private File testFolder = new File("res/Zestaw1/Test");

    private final ArrayList<File> files = new ArrayList<>();
    private final ArrayList<Boolean> genders = new ArrayList<>();

    private File root;

    public static void main(String... args) {
        Main main = new Main();
        if (args.length >= 2)
            switchRoot(main, args);
        if (args[0].equalsIgnoreCase("-train")) {
            main.train();
        } else if (args[0].equalsIgnoreCase("-validate")) {
            main.validate();
        } else if (args[0].equalsIgnoreCase("-test")) {
            main.test();
        } else if (args[0].equalsIgnoreCase("-visualize")) {
            main.visualize();
        } else if (args[0].equalsIgnoreCase("-convert")) {
            ImageProcessor.convertImagesToArrays(main.root);
        } else
            System.err.println("Invalid argument.");
    }

    private static void switchRoot(Main main, String... args) {
        main.root = new File(args[1]);
        main.maleFolder = new File(main.root, "Male");
        main.femaleFolder = new File(main.root, "Female");
        main.testFolder = new File(main.root, "Test");
    }


    private void train() {
        for (File file : femaleFolder.listFiles()) {
            if (!file.isFile() || file.isHidden())
                continue;
            files.add(file);
            genders.add(Boolean.FALSE);
        }

        for (File file : maleFolder.listFiles()) {
            if (!file.isFile() || file.isHidden())
                continue;
            files.add(file);
            genders.add(Boolean.TRUE);
        }

        long seed = 3;
        Collections.shuffle(files, new Random(seed));
        Collections.shuffle(genders, new Random(seed));

        neuralNetwork.trainNetwork(files, genders);

        try (FileOutputStream fout = new FileOutputStream("weights")) {
            neuralNetwork.saveWeights(fout);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void validate() {
        for (File file : femaleFolder.listFiles()) {
            if(!file.isFile() || file.isHidden())
                continue;
            files.add(file);
            genders.add(Boolean.FALSE);
        }

        for (File file : maleFolder.listFiles()) {
            if(!file.isFile() || file.isHidden())
                continue;
            files.add(file);
            genders.add(Boolean.TRUE);
        }

        neuralNetwork.validate(files, genders);
    }

    private void test() {
        for (File file : testFolder.listFiles()) {
            if(!file.isFile() || file.isHidden())
                continue;
            files.add(file);
        }

        try (FileInputStream fin = new FileInputStream("weights")) {
            neuralNetwork.loadWeights(fin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        neuralNetwork.testNetwork(files);
    }

    private void visualize() {
        //reuse fin to get weights
        try (FileInputStream fin = new FileInputStream("weights")) {
            Painter.painter(fin);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
