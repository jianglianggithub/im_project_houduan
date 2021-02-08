package com.jl.common.utils;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpUtil {


    public static String getLocalhostByNetworkInterface() throws SocketException {
        List<String> candidatesHost = new ArrayList<String>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            // Workaround for docker0 bridge
            if ("docker0".equals(networkInterface.getName()) || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress address = addrs.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }
                //ip4 highter priority
                if (address instanceof Inet6Address) {
                    candidatesHost.add(address.getHostAddress());
                    continue;
                }
                return address.getHostAddress();
            }
        }

        if (!candidatesHost.isEmpty()) {
            return candidatesHost.get(0);
        }
        return null;
    }
}
