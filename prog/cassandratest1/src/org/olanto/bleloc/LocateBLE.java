/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc;

/**
 *
 * @author jacques
 */
public class LocateBLE {

    static final int NBRASPI = 11;
    private long[] tsSerie;
    private String bleName;
    private ComputeLocation client;
    private int nbperiod;
    private int[][] avgsignal;
    private float[][] estimatePosition;
    private float x;
    private float y;
     private int[] avgperiod=new int[NBRASPI];

    public LocateBLE(ComputeLocation client, long[] tsSerie, String bleName) {
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

   public void ComputeAvgPeriod() {

        for (int j = 0; j < NBRASPI; j++) {
            for (int i = 0; i < nbperiod; i++) {
                avgperiod[j] += avgsignal[i][j];
            }
            avgperiod[j] /= nbperiod;
        }
        System.out.print("Health raspi: ");
        for (int j = 0; j < NBRASPI; j++) {
            System.out.print(avgperiod[j]+ ", "); 
        }
        System.out.println();
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
