/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc.tri;

import static org.olanto.bleloc.tri.LocateBLETri.NBRASPI;

/**
 *
 * @author jacques
 */
public class LocateBLETri_AvgQuarter {

    static final int NBRASPI = 11;
    private long[] tsSerie;
    private String bleName;
    private ComputeLocationTri_AvgQuarter client;
    private int nbperiod;
    private int[][] avgsignal;
    private int[] avgQuarterperiod = new int[NBRASPI];
    private int[] avgperiod = new int[NBRASPI];
    private float[][] estimatePosition;
    private float x;
    private float y;

    public LocateBLETri_AvgQuarter(ComputeLocationTri_AvgQuarter client, long[] tsSerie, String bleName) {
        this.client = client;
        this.tsSerie = tsSerie;
        this.bleName = bleName;
        nbperiod = tsSerie.length;
        avgsignal = new int[nbperiod][NBRASPI];
        estimatePosition = new float[nbperiod][2];
    }

    public void getMeasures() {
        for (int i = 0; i < nbperiod; i++) {
            for (int j = 0; j < NBRASPI; j++) {
                //System.out.println("getMeasures"+tsSerie[i]+", "+bleName+", "+ "" +( 100 + j));
                avgsignal[i][j] = client.getRaspiAvg(tsSerie[i], bleName, "" + (100 + j));
            }
        }
    }

    public void ComputeQuarterAvgPeriod(int start, int end) {
        for (int j = 0; j < NBRASPI; j++) {
            for (int i = start; i < end; i++) {
                avgQuarterperiod[j] += avgsignal[i][j];
            }
            avgQuarterperiod[j] /= nbperiod / 4;
        }
    }

    public void estimateQuarterPosition(int i) {
        estimatePosition[i] = client.getPosXY(avgQuarterperiod);

    }

    public void estimatePosition() {
        for (int i = 0; i < estimatePosition.length; i++) {
            estimatePosition[i] = client.getPosXY(avgsignal[i]);
        }
    }

    public void finishEstimation() {
        float sumx = 0;
        float sumy = 0;
        for (int i = 0; i < estimatePosition.length; i++) {
            sumx += estimatePosition[i][0];
            sumy += estimatePosition[i][1];
        }
        System.out.println("sumxy " + sumx + ", " + sumy);
        x = sumx / nbperiod;
        y = sumy / nbperiod;
    }

    /**
     * @return the bleName
     */
    public String getBleName() {
        return bleName;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

}
