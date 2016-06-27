package org.olanto.bleloc.tri;

import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.linear.RealVector;

public class Trilateration {

//	public static void main(String[] args) {
//		double[] raspiVal = new double[] { -68, -26, -78, -83, -69, -70, -73, -77, -75, -74, -87 };
//		double[][] raspiPosition = new double[][] { { 18, 0 }, { 12, 0 }, { 6, 0 }, { 0, 0 }, { 15, 5 }, { 9, 5 },
//				{ 3, 5 }, { 18, 10 }, { 12, 10 }, { 6, 10 }, { 0, 10 } };
//		double[] x = Trilateration.getPosXY(raspiVal, raspiPosition, "X");
//		System.out.println(x[0]+" "+x[1]);
//	}
	/**
	 * Get beacon position (x,y).
	 * 
	 * @author houssem ben mahfoudh
	 * @version 0.1
	 * @param raspiVal
	 *            array contains RSSI
	 * @param raspiPosition
	 *            array contains raspberries position[] with position is
	 *            double{x,y}
	 * @param beaconName
	 *            beacon's name
	 * @return double[]: position x,y
	 */
    
        final static int NBRASPI=5;
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
			double[] distances = new double[NBRASPI];
			double[][] positions = new double[NBRASPI][2];
			map = sortByValue(map);
			
			int i = 0;
			for (Entry<double[], Double> entry : map.entrySet()) {
				positions[i] = entry.getKey();
				distances[i] = entry.getValue();
				i++;
				if (i == NBRASPI)
					break;
			}
                        if (distances[0]<0.5){
                            return positions[0];
                        }
			TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
			LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(trilaterationFunction);
			NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction,
					new LevenbergMarquardtOptimizer());

			RealVector x = lSolver.solve();
			Optimum optimum = nlSolver.solve();
			// non linear
			double[] calculatedPosition = optimum.getPoint().toArray();
			// linear
			double[] linc = x.toArray();
                        linc[0]=Math.max(Math.min(18,linc[0]),0);
                       linc[1]=Math.max(Math.min(10,linc[1]),0);
			return linc;
			//return calculatedPosition;
		} else
			return null;
	}

	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
