/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.bleloc.test;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.bleloc.GetDataFromFile;
import static org.olanto.util.Messages.msg;

/**
 *
 * @author jacques
 */
public class TestSystemIn {

    private Cluster cluster;
    private Session session;

    public static final String WINDOWS_FAMILIES = "WINDOWS_FAMILIES";
    public static final String UNIX_FAMILIES = "UNIX_FAMILIES";
    private static final String DEF_WINDOWS_HOME = "C:/";
    private static final String DEF_UNIX_HOME = "/home/olanto/";
    private static String OS_TYPE = null;
    private static Map<String, String> env;
    private static String root;
    private static String raspid;
    static HashMap<String, String> macId = new HashMap<>();
    
        private PreparedStatement stat_insert2rawsearch;
    private BoundStatement bound_insert2rawsearch;

    private PreparedStatement stat_insert2raw2search;
    private BoundStatement bound_insert2raw2search;

    static int count=0;

    public static void main(String args[]) {

        TestSystemIn client = new TestSystemIn();
        

       

        System.out.println(getOS_TYPE());
        if (OS_TYPE.equals("WINDOWS_FAMILIES")) {
            root = "F:/"; 
            client.connect("192.168.40.129");
        } else {
            root = "/media/KINGSTON/";
            client.connect("192.168.0.246");
        }
        getRaspID(root + "properties");
        getParameters(root + "macshortid.txt");
        
        client.insert2rawsearch(1462606130L,1462606137133L,"100","A234",-58,-85);
        
       loopForEver(client);
        client.close();

    }

    public Session getSession() {
        return this.session;
    }

    public void connect(String node) {
        cluster = Cluster.builder()
                .addContactPoint(node)
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
    }

    public void close() {
        session.close();
        cluster.close();
    }

        public void insert2rawsearch(long ts10, long ts,String raspid, String shortname, int rssi1, int a) {
        if (bound_insert2rawsearch == null) {
            stat_insert2rawsearch = session.prepare(
                    "insert into repoble.rawsearch(ts10, ts, raspid, shortname, rssi1, a)"
                    + " VALUES (?,?,?,?,?,?);");
            bound_insert2rawsearch = new BoundStatement(stat_insert2rawsearch);
        }
        session.execute(bound_insert2rawsearch.bind(ts10, ts, raspid, shortname, rssi1, a));
        insert2raw2search(ts10, shortname, false); // mark event to process it
        // client.insert2rawinfo("TTT","TRAINING",1462606130L,1462606137133L,"100","cb:d1:5a:06:b3:d7","A234",0,10,-58,-85);
    }

    public void insert2raw2search(long ts10,String shortname, boolean processed) {
        if (bound_insert2raw2search == null) {
            stat_insert2raw2search = session.prepare(
                    "insert into repoble.raw2search( ts10,  shortname, processed)"
                    + " VALUES (?,?,?);");
            bound_insert2raw2search = new BoundStatement(stat_insert2raw2search);
        }
        session.execute(bound_insert2raw2search.bind(ts10,  shortname, processed));

        // client.insert2raw2process("TTT","TRAINING",1462606130L,"100","A234",false);
    }

    
    public static void getRaspID(String filename) {
        InputStreamReader data = null;
        try {
            data = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader in = new BufferedReader(data);
            String MAC = in.readLine();
            System.out.println(MAC);
            String IP = in.readLine();
            System.out.println(IP);
            raspid = IP.substring(IP.length() - 3, IP.length());
            System.out.println("RaspID=" + raspid);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetDataFromFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
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
            String w = in.readLine();
            while (w != null) {
                System.out.println(w);
                String[] part = w.split("\t");
                //ca:bf:14:f6:65:05	StickNFind	A100	070-105
                macId.put(part[0], part[2]);
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

    public static String getOS_TYPE() {
        if (OS_TYPE == null) {//
            String runningOS = System.getProperty("os.name");
            msg("running OS:" + runningOS);
            if (runningOS.startsWith("Window")) {
                OS_TYPE = WINDOWS_FAMILIES;
            } else {  // pour le moment tous les autres sont des Unix !!!
                OS_TYPE = UNIX_FAMILIES;
            }
            env = System.getenv();
        }
        return OS_TYPE;
    }

       public static void convert(TestSystemIn client, String w) {
                String[] part = w.split(",");
                if (part.length == 7) {
                long ts = Long.parseLong(part[0]) / 1000;
                 //System.out.println(part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|" + part[4] + "|" + part[5] + "|" + part[6] + "|");
                    long tic10 = ts / 10 * 10;
                    String bleid = macId.get(part[1]);
                    if (bleid != null) {
                         count++;
                        if (count % 1000 == 0) {
                            System.out.println("count="+count);
                        }
                        //insert2rawsearch(long ts10, long ts,String raspid, String shortname, int rssi1, int a)
                        if (raspid.equals("999")){ // simulate all raspi
                           for (int i=100; i<=110;i++) 
                               client.insert2rawsearch(tic10, Long.parseLong(part[0]),""+i, bleid, Integer.parseInt(part[5]), Integer.parseInt(part[6]));                      
                        }
                      else  client.insert2rawsearch(tic10, Long.parseLong(part[0]),raspid, bleid, Integer.parseInt(part[5]), Integer.parseInt(part[6])
                        );
                    }
                    //else System.out.println("error unknown macid "+part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|"+ part[4] + "|"+ part[5] + "|"+ part[6] +  "|");
                }   
    }
    
    public static void loopForEver(TestSystemIn client) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String w;
        while (true) {
            try {
                w = br.readLine();
                if (w == null) {
                    Thread.sleep(1000);
                    //System.out.println("sleep");
                } else {
                    System.out.println(w);
                    convert(client,w);
                }
            } catch (IOException ex) {
                Logger.getLogger(TestSystemIn.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestSystemIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    
}
