package org.olanto.bleloc;

import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class TestMAC {
    
    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
       
        out.printf("Up? %s\n", netint.isUp());
        out.printf("Loopback? %s\n", netint.isLoopback());
        out.printf("PointToPoint? %s\n", netint.isPointToPoint());
        out.printf("Supports multicast? %s\n", netint.supportsMulticast());
        out.printf("Virtual? %s\n", netint.isVirtual());
        
        byte[] mac = netint.getHardwareAddress();
        
        out.printf("Hardware address: %s\n",
                    Arrays.toString(mac));
        
        //Print MAC (Hardware address) in HEX format
        if(mac != null){
            StringBuilder sbMac = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sbMac.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));      
            }
            out.printf("Hardware address (HEX): [%s]\n", sbMac.toString());
        }
                    
        out.printf("MTU: %s\n", netint.getMTU());
        out.printf("\n");
     }
}
