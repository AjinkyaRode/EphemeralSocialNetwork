package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.net.wifi.p2p.WifiP2pDevice;


/*
 * Class to keep information of current service including
 * device, service name and registration type
 */
public class P2PService {
    WifiP2pDevice device;
    String serviceName = null;
    String serviceType = null;
}