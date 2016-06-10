/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simple
 */
public class GetDataFromFile {

    static String expName = "";
    static long start, stop;
    static HashMap<String, String> macId = new HashMap<>();
    static BufferedWriter outdata = null;
    static final int LAST_RASPI=108;
    
    public static void main(String[] args) {
        try {
            String exp = "parcours.txt";
         //   String root = "C:\\Users\\jacques\\Desktop\\CLIENTS\\CUINOLAB\\Experiments\\expbiblio\\experiment\\";
           String root = "C:\\Users\\jacques\\Desktop\\CLIENTS\\CUINOLAB\\Experiments\\exp20160507\\experiment\\";
            getParameters(root + exp);
            System.out.println(expName + " from: " + start + " to: " + stop);
            outdata = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(root + expName + ".data"), "UTF-8"));

            for (int i = 100; i <= LAST_RASPI; i++) {
                convert(root + i + "/log.txt", i);
            }
            outdata.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void getParameters(String filename) {
        InputStreamReader data = null;
        try {
            data = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader in = new BufferedReader(data);
            expName = in.readLine();
            start = Long.parseLong(in.readLine());
            stop = Long.parseLong(in.readLine());
            String w = in.readLine();
            while (w != null) {
                String[] part = w.split("\t");
                //ca:bf:14:f6:65:05	StickNFind	A100	070-105
                macId.put(part[0], part[2] + "\t" + part[3]);
                w = in.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void convert(String filename, int raspi) {
        InputStreamReader data = null;
        int count = 0;
        try {
            data = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader in = new BufferedReader(data);

            String w = in.readLine();

            while (w != null) {

                String[] part = w.split(",");
                if (part.length == 7) {
                    long ts = Long.parseLong(part[0]) / 1000;

                    if (ts >= start && ts <= stop) {

                        //System.out.println(part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|"+ part[4] + "|"+ part[5] + "|"+ part[6] +  "|");
                        long tic10 = ts / 10 * 10; // 10 secondes
                       
                        // long tic10 = ts ; // 1 seconde !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                         
                         
                        int reverse = Math.max(100 + Integer.parseInt(part[6]), 0);
                        String res = macId.get(part[1]);
                        if (res != null) {
                            count++;
                            outdata.append(w + "\t" + raspi + "\t" + tic10 + "\t" + reverse + "\t" + res + "\n");
                        }
                        //else System.out.println("error unknown macid "+part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|"+ part[4] + "|"+ part[5] + "|"+ part[6] +  "|");
                    }
                } 
                //else {System.out.println("uncomplete line:" + w);}
                w = in.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                data.close();
            } catch (IOException ex) {
                Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println(raspi + ": #" + count);
    }

}
