package org.olanto.bleloc;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollectFromRaspi {

    private Cluster cluster;
    private Session session;

    private PreparedStatement stat_insert2rawinfo;
    private BoundStatement bound_insert2rawinfo;

    private PreparedStatement stat_insert2raw2process;
    private BoundStatement bound_insert2raw2process;

    static String expName = "";
    static long start, stop;
    static HashMap<String, String> macId = new HashMap<>();

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

    public void insert2rawinfo(String period, String mode, long ts10, long ts,
            String raspid, String mac, String shortname, String locx, String locy,
            int rssi1, int a) {
        if (bound_insert2rawinfo == null) {
            stat_insert2rawinfo = session.prepare(
                    "insert into repoble.rawinfo(period, mode, ts10, ts, raspid, mac, shortname, locx, locy, rssi1, a)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?);");
//insert into repoble.rawinfo(period, mode, ts10, ts, raspid, mac, shortname, locx, locy, rssi1, a)
//VALUES ('TEST','TRAINING',1462606130,1462606137133,
//        '100','cb:d1:5a:06:b3:d7','A234',0,10,
//        -58,-85);

            bound_insert2rawinfo = new BoundStatement(stat_insert2rawinfo);
        }
        session.execute(bound_insert2rawinfo.bind(period, mode, ts10, ts, raspid, mac, shortname, locx, locy, rssi1, a));
        insert2raw2process(period, mode, ts10, shortname, false); // mark event to process it
        // client.insert2rawinfo("TTT","TRAINING",1462606130L,1462606137133L,"100","cb:d1:5a:06:b3:d7","A234",0,10,-58,-85);
    }

    public void insert2raw2process(String period, String mode, long ts10,
            String shortname, boolean processed) {
        if (bound_insert2raw2process == null) {
            stat_insert2raw2process = session.prepare(
                    "insert into repoble.raw2process(period, mode, ts10,  shortname, processed)"
                    + " VALUES (?,?,?,?,?);");
            bound_insert2raw2process = new BoundStatement(stat_insert2raw2process);
        }
        session.execute(bound_insert2raw2process.bind(period, mode, ts10,  shortname, processed));

        // client.insert2raw2process("TTT","TRAINING",1462606130L,"100","A234",false);
    }

    public static void initExternalInfo(CollectFromRaspi client) {

        String exp = "testlignegilles.txt";
        String root = "C:\\Users\\jacques\\Desktop\\CLIENTS\\CUINOLAB\\Experiments\\expbiblio\\experiment\\";
        getParameters(root + exp);
        System.out.println(expName + " from: " + start + " to: " + stop);

        for (int i = 100; i <= 110; i++) {
            convert(client, root + i + "/log.txt", i);
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

    public static void convert(CollectFromRaspi client, String filename, int raspi) {
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

                    //System.out.println(part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|" + part[4] + "|" + part[5] + "|" + part[6] + "|");
                    long tic10 = ts / 10 * 10;
                    int reverse = Math.max(100 + Integer.parseInt(part[6]), 0);
                    String res = macId.get(part[1]);

                    if (res != null) {
                        String[] idloc = res.split("\t");
                        String bleid = idloc[0];
                        String[] loc = idloc[1].split("-");
                        count++;
                        if (count % 1000 == 0) {
                            System.out.println(count);
                        }
                        client.insert2rawinfo("TEST", "T", tic10, Long.parseLong(part[0]),
                                "" + raspi, part[1], bleid, loc[0], loc[1],
                                Integer.parseInt(part[5]), Integer.parseInt(part[6])
                        );

                    }
                    //else System.out.println("error unknown macid "+part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|"+ part[4] + "|"+ part[5] + "|"+ part[6] +  "|");
                }
                }
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

    public static void main(String[] args) {

        CollectFromRaspi client = new CollectFromRaspi();
        // client.connect("192.168.0.246");
        client.connect("192.168.40.143");

        initExternalInfo(client);

        client.close();

    }

}
