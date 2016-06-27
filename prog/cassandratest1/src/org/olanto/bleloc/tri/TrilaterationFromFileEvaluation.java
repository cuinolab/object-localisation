/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc.tri;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author jacques
 */
public class TrilaterationFromFileEvaluation {

    static double[][] positions = new double[][]{ // raspi position
        {18.0, 0.0}, // 100
        {12.0, 0.0}, // 101
        {6.0, 0.0}, // 102
        {0.0, 0.0}, // 103
        {15.0, 5.0}, // 104
        {9.0, 5.0}, // 105
        {3.0, 5.0}, // 106
        {18.0, 10.0}, // 107
        {12.0, 10.0}, // 108
        {6.0, 10.0}, // 109
        {0.0, 10.0} // 110
    };

    public static void main(String[] args) {
        //simpletest();
                    String exp = "test1&det1-2.dataclass";
            String root = "C:\\Users\\jacques\\Desktop\\CLIENTS\\CUINOLAB\\object-localisation\\Experiments\\expbiblio\\experiment\\";
 
        compute4File(root+exp);
    }

    public static void simpletest() {
        double[] signal = new double[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 42};
        double[] distances = dist(signal);

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        Optimum optimum = solver.solve();

        // the answer
        double[] centroid = optimum.getPoint().toArray();

        for (int i = 0; i < centroid.length; i++) {
            System.out.println(centroid[i]);
        }

    }

    static double[] dist(double[] signal) {
        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            if (signal[i] != 0) {
                double rssi = 100 - signal[i];
                result[i] = Math.pow(10, (rssi - 58) / (10 * 3));
            }
            //System.out.println(signal[i] + "->" + result[i]);
        }
        return result;
    }

    public static void compute4File(String filename) {
      
        try {
            InputStreamReader data = null;
            int count = 0;
            data = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader in = new BufferedReader(data);

            String w = in.readLine();

            while (w != null) {
                String[] part = w.split("\t");

                double[] val = new double[11];
                for (int i = 3; i < 14; i++) {
                    val[i - 3] = (double) Integer.parseInt(part[i])-100;
                }
                for (int i = 0; i < 11; i++) {
//            if (i==0) val[i]=0;
            if (i==1) val[i]=0;
            if (i==2) val[i]=0;
//            if (i==3) val[i]=0;
            if (i==4) val[i]=0;
            if (i==5) val[i]=0;
            if (i==6) val[i]=0;
//            if (i==7) val[i]=0;
            if (i==8) val[i]=0;
            if (i==9) val[i]=0;
//            if (i==10) val[i+3]=0;
        }
                double[] centroid=Trilateration.getPosXY(val, positions, part[0]);
                String[] pos=part[1].split("-");
                double rx=((double) Integer.parseInt(pos[0]))/10;
                double ry=((double) Integer.parseInt(pos[1]))/10;
                double px=centroid[0];
                double py=centroid[1];
                double ecart=Math.sqrt((rx-px)*(rx-px)+(ry-py)*(ry-py));
                System.out.println(w + "\t" + px + "\t" + py + "\t" +rx + "\t" +ry+ "\t" +ecart);
                w = in.readLine();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TrilaterationFromFileEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TrilaterationFromFileEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrilaterationFromFileEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
             
      }

}
