package org.olanto.bleloc;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetCurrentLocation {
    
    private Cluster cluster;
    private Session session;
    
    static String expName = "";
    static long start, stop;
    static Vector<String> shortName = new Vector<>();
    
    static BufferedWriter outdata = null;
    private PreparedStatement stat_insert2currentxy;
    private BoundStatement bound_insert2currentxy;
    
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

     public long last2Process() {
        // select count(*) nb2proc from repoble.raw2process where period = 'TEST' and mode='T' and processed=false;
        ResultSet results = session.execute("SELECT max(ts10) as count  FROM repoble.raw2search "
                + "WHERE  processed=false");
        long res = 0;
        for (Row row : results) {
            res = row.getLong("count");
            //System.out.println(res);
        }
        //System.out.println();
        return res;
    }
    
     
    public String getCurrentXY() {
        // select raspid, avg(a) from repoble.rawinfo WHERE period = shortname='X4' and ts10=1463748990 and raspid='101';
        ResultSet results = session.execute("SELECT shortname,x,y FROM repoble.currentxy");
        String result ="";
        DecimalFormat df= new DecimalFormat("#.#");
        for (Row row : results) {
            String shortname = row.getString("shortname");
            double x = row.getDouble("x");
            double y = row.getDouble("y");
            result += shortname+"\t"+df.format(x)+"\t"+df.format(y)+"\n";
        }
        //System.out.println(ts10+","+shortname+","+raspid+" avg="+ts10+","+ts10+","+ts10+","+result);
        return result;
    }
    
   
    public static void main(String[] args) {
         GetCurrentLocation client = new GetCurrentLocation();
        client.connect("192.168.0.246");
        // client.connect("192.168.40.143");

        while (true) {
            System.out.println(client.last2Process());
            System.out.println(client.getCurrentXY());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GetCurrentLocation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //getPrediction();
        //client.list2Process(period, mode);
        //client.close();
    }
    
}
