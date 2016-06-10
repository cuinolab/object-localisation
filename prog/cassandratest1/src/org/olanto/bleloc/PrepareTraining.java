package org.olanto.bleloc;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
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

public class PrepareTraining {

    private Cluster cluster;
    private Session session;

 
    static String expName = "";
    static long start, stop;
    static HashMap<String, String> macId = new HashMap<>();

    static BufferedWriter outdata = null;
     static final int LAST_RASPI=108;
    
    
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

    public void howMany2Process(String period, String mode) {
        // select count(*) nb2proc from repoble.raw2process where period = 'TEST' and mode='T' and processed=false;
        ResultSet results = session.execute("SELECT count(*) as count  FROM repoble.raw2process "
                + "WHERE period = '" + period + "' and mode='" + mode + "' and processed=false");
        for (Row row : results) {
            System.out.println(row.getLong("count"));
        }
        System.out.println();
    }

    public void list2Process(String period, String mode) {
        // select count(*) nb2proc from repoble.raw2process where period = 'TEST' and mode='T' and processed=false;
        ResultSet results = session.execute("SELECT * FROM repoble.raw2process "
                + "WHERE period = '" + period + "' and mode='" + mode + "' and processed=false");
        for (Row row : results) {
            long ts10 = row.getLong("ts10");
            String shortname = row.getString("shortname");
            String res = macId.get(shortname);
            if (res != null) {
                try {
                    System.out.print(shortname + "\t"+ res + "\t" + ts10);
                    outdata.append(shortname + "\t"+ res + "\t" + ts10);
                    for (int raspid = 100; raspid <= LAST_RASPI; raspid++) {
                        String avg="\t" + getRaspiAvg(period, mode, ts10, shortname, "" + raspid);
                        System.out.print(avg);
                        outdata.append(avg);
                    }
                    System.out.print("\n");
                    outdata.append("\n");
                } catch (IOException ex) {
                    Logger.getLogger(PrepareTraining.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("No location for:" + shortname);
            }
        }
        System.out.println();
    }

    public int getRaspiAvg(String period, String mode, long ts10, String shortname, String raspid) {
        // select raspid, avg(a) from repoble.rawinfo WHERE period = 'TEST' and mode='T' and shortname='X4' and ts10=1462617600 and raspid='101';
        ResultSet results = session.execute("SELECT avg(a) as avg FROM repoble.rawinfo "
                + "WHERE period = '" + period + "' and mode='" + mode
                + "' and shortname='" + shortname + "' and ts10=" + ts10 + " and raspid='" + raspid + "'");
        int result = 0;
        for (Row row : results) {
            int a = row.getInt("avg");
            if (a == 0) {
                return 0;
            } else {
                return Math.max(100 + row.getInt("avg"), 0);
            }

        }
        return result;
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
                macId.put(part[2], part[3]);
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

    public static void main(String[] args) {
        try {
            PrepareTraining client = new PrepareTraining();
            String exp = "parcours.txt";
            String root = "C:\\Users\\jacques\\Desktop\\CLIENTS\\CUINOLAB\\Experiments\\exp20160507\\experiment\\";
            client.getParameters(root + exp);
            //    client.connect("192.168.0.246");
            client.connect("192.168.40.131");
            
            String period = "TEST";
            String mode = "T";
            outdata = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(root + exp + ".dataforclass"), "UTF-8"));
            
            client.howMany2Process(period, mode);
            client.list2Process(period, mode);
            
            client.close();
            outdata.close();
        } catch (IOException ex) {
            Logger.getLogger(PrepareTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
