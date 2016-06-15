package org.olanto.bleloc.tri;

import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.linear.RealVector;

public class Trilateration {

    /**
     * Get beacon position (x,y).
     *
     * @author houssem ben mahfoudh
     * @version 0.1
     * @param raspiVal array contains RSSI
     * @param raspiPosition array contains raspberries position[] with position
     * is double{x,y}
     * @param beaconName beacon's name
     * @return double[]: position x,y
     */
    public static double[] getPosXY(double[] raspiVal, double[][] raspiPosition, String beaconName) {
        if (raspiVal.length == raspiPosition.length) {
            Map<double[], Double> map = new LinkedHashMap<>();
            for (int i = 0; i < raspiVal.length; i++) {
                if (beaconName.startsWith("X")) {
                    map.put(raspiPosition[i], Utils.calculateDistance(raspiVal[i], -58));
                } else if (beaconName.startsWith("B")) {
                    map.put(raspiPosition[i], Utils.calculateDistance(raspiVal[i], -59));
                } else {
                    map.put(raspiPosition[i], Utils.calculateDistance(raspiVal[i], -59));
                }

            }
            double[] distances = new double[4];
            double[][] positions = new double[4][2];
            map = sortByValue(map);

            int i = 0;
            for (Entry<double[], Double> entry : map.entrySet()) {
                positions[i] = entry.getKey();
                distances[i] = entry.getValue();
                i++;
                if (i == 4) {
                    break;
                }
            }
            if (distances[0] < 0.6) {
                return positions[0];

            } else {
                TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
                LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(trilaterationFunction);
                NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction,
                        new LevenbergMarquardtOptimizer());

                RealVector x = lSolver.solve();
                Optimum optimum = nlSolver.solve();
                // non linear
                double[] calculatedPosition = optimum.getPoint().toArray();
                // linear
                double[] linearCalculatedPosition = x.toArray();

                return linearCalculatedPosition;
            }
            //return calculatedPosition;
        } else {
            return null;
        }
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
