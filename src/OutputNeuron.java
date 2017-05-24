import java.util.ArrayList;

public class OutputNeuron extends Neuron {
    
    OutputNeuron(final int numInputs, ArrayList<Double> inputs, double initalWeightClamp) {
        super(numInputs, inputs, initalWeightClamp);
    }
    
    public void computeGradient(final double targetOutput) {
        double prediction = output;
        double actual = targetOutput;
        double error =(targetOutput - output);
        double weightChange = //backward pass
        gradient = output * (1 - output) * (targetOutput - output);
    }
}
