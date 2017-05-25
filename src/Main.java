import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    private File maleFolder = new File("res/Set2/Male");
    private File femaleFolder = new File("res/Set2/Female");
    private File testFolder = new File("res/Set2/Test");

    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<Boolean> genders = new ArrayList<>();

    private File root;
    private List<Data> data;

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
        data = new ArrayList<>();
        File[] males = maleFolder.listFiles();
        File[] females = femaleFolder.listFiles();
        int size = males.length > females.length ? males.length : females.length;
        for (int i = 0; i < size; i++) {

            if (i < males.length && !males[i].isHidden() && males[i].isFile())
                data.add(new Data(males[i], true));
            if (i < females.length && !females[i].isHidden() && females[i].isFile())
                data.add(new Data(females[i], false));
        }
        double accuracy;
        for (int i = 0; i < 50; i++) {
            accuracy = neuralNetwork.trainNetwork(data);
            System.out.println("Accuracy: " + accuracy);
            if(accuracy >= NeuralNetwork.LEARNING_ACCURACY)
                break;
        }

        try (FileOutputStream fout = new FileOutputStream("weights")) {
            neuralNetwork.saveWeights(fout);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void test() {
        files = new ArrayList<>();
        for (File file : testFolder.listFiles()) {
            if (!file.isFile() || file.isHidden())
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

    public void validate() {
        files = new ArrayList<>();
        genders = new ArrayList<>();
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

        neuralNetwork.validate(files, genders);
    }


    public void visualize() {
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
