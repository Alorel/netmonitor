/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Art on 09/12/2016.
 */
public class Main {

    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
    }
}
