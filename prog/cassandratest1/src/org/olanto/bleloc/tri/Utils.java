package org.olanto.bleloc.tri;
import java.util.ArrayList;

 class Utils {

	/*
	 * @return distance measured from RSSI
	 */
	public static Double calculateDistance(double rssi, double txPower) {
		double ratio;
		ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double distance;
			distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			// distance = Math.pow(10, (-rssi + txPower) / (10 * 2));
			// if (distance < Threshold)
			return distance;
			// else
			// return Threshold;
		}
	}

	/*
	 * @return average of Integer's list
	 */
	public static double average(ArrayList<Integer> array) {
		Integer sum = 0;
		if (!array.isEmpty()) {
			for (Integer mark : array) {
				sum += mark;
			}
			return sum.doubleValue() / array.size();
		}
		return sum;

	}

}
