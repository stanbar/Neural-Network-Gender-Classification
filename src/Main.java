import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    private File maleFolder = new File("res/Set2/Male");
    private File femaleFolder = new File("res/Set2/Female");
    private File testFolder = new File("res/Set2/Test");

    private final ArrayList<File> files = new ArrayList<>();
    private final ArrayList<Boolean> genders = new ArrayList<>();

    private File root;

    public static void main(String... args) {
        Main main = new Main();
        if (args.length >= 2)
            main.switchRoot(args);
        if (args[0].equalsIgnoreCase("-train")) {
            main.train();
        } else if (args[0].equalsIgnoreCase("-validate")) {
            main.validate();
        } else if (args[0].equalsIgnoreCase("-test")) {
            main.test();
        } else if (args[0].equalsIgnoreCase("-visualize")) {
            main.visualize();
        } else if (args[0].equalsIgnoreCase("-convert")) {
            main.convert();

        } else
            System.err.println("Invalid argument.");
    }




    public void switchRoot(String... args) {
        root = new File(args[1]);
        maleFolder = new File(root, "Male");
        femaleFolder = new File(root, "Female");
        testFolder = new File(root, "Test");
    }


    public void train() {
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

    public void validate() {
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

    public void test() {
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
    public void convert() {
        ImageProcessor.convertImagesToArrays(root);
    }

}
