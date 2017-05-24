import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * Created by stasbar on 22.05.2017.
 */
public class Painter {
    public static void painter(FileInputStream fin) {
        double[] store = new double[120 * 128];
        byte[] buffer = new byte[(15360 + 1) * 8];
        ByteBuffer b = ByteBuffer.wrap(buffer);
        DoubleBuffer db = b.asDoubleBuffer();

        try {
            while(fin.available() > 0) {
                fin.read(buffer, 0, (15360 + 1) * 8);
                db.get();
                db.get(store);
                db.rewind();
                paint(store);
            }
        }
        catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }


    }


    public static void paint(double[] dataAsDouble) {

        // normalize all numbers
        double normhigh = 255, normlow = 0;
        double datamax = 0, datamin = 255;
        for (int k = 0; k < dataAsDouble.length; k++) {
            if (dataAsDouble[k] > datamax) {
                datamax = dataAsDouble[k];
            }
            if (dataAsDouble[k] < datamin) {
                datamin = dataAsDouble[k];
            }
        }

        int data[] = new int[120 * 128];
        for (int i = 0; i < dataAsDouble.length; i++){
            data[i] = (int) denormalize(dataAsDouble[i], datamin, datamax, normlow, normhigh);
        }


        //buffer image
        BufferedImage image = new BufferedImage(128, 120, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setPixels(0, 0, 128, 120, data);

        // display everything with a JFrame and a Jlabel using an icon
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }


    // modified from http://www.heatonresearch.com/wiki/Range_Normalization
    // normalizes a number to based on range
    // @param number, highs and lows
    public static double denormalize(double x,double dataLow,double dataHigh,double normalizedLow, double normalizedHigh) {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
}
