package anwendung;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class HostData
{

    // EINFACH
    // (Gefahr dass Loopback IP genommen wird)

    // Hostname
    public static String getName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e1)
        {
            e1.printStackTrace();
        } // trycatch
        return "unknown host";
    }// getName

    // IP
    public static String getIP()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e1)
        {
            e1.printStackTrace();
        } // trycatch
        return "unknown ip";
    }// getName

    // Aufwändige Version

    // Starter Methode - try-catch block
    public static String getHost()
    {
        String feedback = "";
        try
        {
            feedback = (HostData.getHostAndIP());
        }
        catch (SocketException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return feedback;
    }//

    // liest Netzwerkinterfaces - Router aus - throws (wird von starter
    // gefangen)
    private static String getHostAndIP() throws SocketException
    {

        String hostip = "";

        Enumeration<NetworkInterface> netfaces = NetworkInterface.getNetworkInterfaces();

        while (netfaces.hasMoreElements())
        {
            NetworkInterface netface = netfaces.nextElement();

            // ALLGEMEIN -> ROUTERADRESSE (geht nach routerdisplay)
            // beer-beerbel.fritz.box , 192.168.178.30 bei lan / .24 bei wlan
            for (InetAddress address : Collections.list(netface.getInetAddresses()))
            {
                if (address.isLoopbackAddress() != true && address.isSiteLocalAddress() == true)
                {
                    hostip = (address.getHostName() + "\n" + address.getHostAddress());
                } // if
            } // foreach

        } // while

        return hostip;
    }// getHostAndIP

}// Network
