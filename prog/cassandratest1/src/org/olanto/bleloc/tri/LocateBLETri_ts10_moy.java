/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc.tri;

/**
 *
 * @author jacques
 */
public class LocateBLETri_ts10_moy {

    static final int NBRASPI = 11;
    private long[] tsSerie;
    private String bleName;
    private ComputeLocationTri_ts10_moy client;
    private int nbperiod;
    private int[][] avgsignal;
    private float[][] estimatePosition;
    private float x;
    private float y;

    public LocateBLETri_ts10_moy(ComputeLocationTri_ts10_moy client, long[] tsSerie, String bleName) {
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

    public void estimatePosition() {
        for (int i = 0; i < nbperiod; i++) {
            estimatePosition[i] = client.getPosXY(avgsignal[i]);
        }
    }

   public void finishEstimation() {
       float sumx=0;      float sumy=0;
        for (int i = 0; i < nbperiod; i++) {
            sumx+=estimatePosition[i][0];
            sumy+=estimatePosition[i][1];
        }
        System.out.println("sumxy "+sumx+", "+sumy);
        x=sumx/nbperiod;
        y=sumy/nbperiod;
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
