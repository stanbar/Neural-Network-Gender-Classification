import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public abstract class Neuron {
    protected int numInputs;
    protected double[] inputWeights;
    protected ArrayList<Double> inputs;
    protected double output; // [0,1]
    protected double gradient;
    protected double bias;

    Neuron(final int numInputs, ArrayList<Double> inputs, double initalWeightClamp) {
        this.numInputs = numInputs;
        this.inputs = inputs;
        double biasValue = 1;
        bias = biasValue * getRandomWeight(initalWeightClamp);
        inputWeights = new double[numInputs];
        for (int i = 0; i < numInputs; i++) {
            inputWeights[i] = getRandomWeight(initalWeightClamp);
        }
    }

    private double getRandomWeight(double initalWeightClamp) {
        return 2 * (Math.random() - 0.5) * initalWeightClamp;
    }

    public void computeOutput() {
        double newOutput = bias;
        for (int i = 0; i < numInputs; i++) {
            double inputWeight = inputWeights[i];
            double input = inputs.get(i);
            newOutput += inputWeight * input;
        }
        output = sigmoidActivationFunction(newOutput);
    }

    public double getOutput() {
        return output;
    }

    public double getGradient() {
        return gradient;
    }

    public void updateWeights(final double learningFactor) {
        bias += learningFactor * gradient;
        for (int i = 0; i < numInputs; i++) {
            inputWeights[i] += learningFactor * gradient * inputs.get(i);
        }
    }

    public double getWeight(int index) {
        return inputWeights[index];
    }

    protected void saveWeights(FileOutputStream fout) {
        ByteBuffer b = ByteBuffer.allocate((numInputs + 1) * 8);
        DoubleBuffer db = b.asDoubleBuffer();
        db.put(bias);
        db.put(inputWeights);

        try {
            fout.write(b.array());
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
    }

    protected void loadWeights(FileInputStream fin) {
        byte[] buffer = new byte[(numInputs + 1) * 8];

        try {
            fin.read(buffer, 0, (numInputs + 1) * 8);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }

        ByteBuffer b = ByteBuffer.wrap(buffer);
        DoubleBuffer db = b.asDoubleBuffer();
        bias = db.get();
        db.get(inputWeights);
    }

    /**
     * Sigmoid activation function
     *
     * @param x value
     * @return value in range of <0,1>
     */
    protected double sigmoidActivationFunction(double x) {
        return 1.0 / (1.0 + Math.exp(-x)); //Sigmoid activation function
    }

    /**
     * Hyperbolic Tangent activation function
     *
     * @param x value
     * @return value in range of <-1,1>
     */
    protected double hyperbolicTangentActivationFunction(double x) {
        return Math.tanh(x);
    }
}
