package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 8/4/15.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

/*
 * A BroadcastReceiver receives the Intents sent by the broadcast service
 * requests. It informs the service of the P2P events
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Activity activity;

    public MyBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                               Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /**
     * Receives the Intent broadcast and while discovering the peers
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /*
         * Indicates that state of Wi-Fi p2p connectivity has changed.
         */
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }

            /*
             * Provides the network info in the form of a NetworkInfo
             */
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {

                /*
                 * Requests the connection information about the connected device
                 */
                manager.requestConnectionInfo(channel,
                        (WifiP2pManager.ConnectionInfoListener) activity);
            } else {
                // It's a disconnect
            }

            /*
             * Broadcast intent action indicating that the
             * current device details have changed
             */
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {

            /*
             * Receives Extra information from the device
             */
            WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        }
    }
}