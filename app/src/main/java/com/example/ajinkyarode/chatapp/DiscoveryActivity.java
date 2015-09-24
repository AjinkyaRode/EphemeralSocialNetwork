package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * Service Discovery Activity class used to register, discover
 * and connect to other services of same type
 */
public class DiscoveryActivity extends Activity implements
        ServiceList.DeviceClickListener, Handler.Callback, ChatFragment.Message,
        WifiP2pManager.ConnectionInfoListener {

    MainActivity usname;
    public static final String myservice = "Ephemeral Social Network";
    public static final String serviceType = "_presence._tcp";
    public static final int read = 1;
    public static final int write = 2;
    private ChatFragment chatFragment;
    private ServiceList serviceList;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ServiceList.WiFiDevicesAdapter adapter;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private final IntentFilter intent = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private String cat=usname.ctr;
    private String name = usname.uname;
    private Handler handler = new Handler(this);
    static final int port = 4545;

    public Handler getHandler() {
        return handler;
    }
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_discovery);

        /*
         * Different actions added to the intent for listening to the ongoing WiFi activity
         */
        intent.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intent.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intent.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intent.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        registerDevice();
        serviceList = new ServiceList();
        getFragmentManager().beginTransaction().add(R.id.root, serviceList, "services").commit();
    }

    /*
     * Register a local service/device
     */
    private void registerDevice() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo1 = WifiP2pDnsSdServiceInfo.newInstance(
                myservice, serviceType, record);
        manager.addLocalService(channel, serviceInfo1, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int error) {
            }
        });

        /*
         * To change the WiFi display name of the device
         */
        try {
            Method m = manager.getClass().getMethod("setDeviceName", new Class[]{channel.getClass(), String.class,
                    WifiP2pManager.ActionListener.class});
            m.invoke(manager, channel, name +" (" +cat + ")", new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        /*
         * Register listeners for DNS-SD services for callbacks that are
         * invoked by the app when a service is discovered
         */
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String service1,
                                                        String reg_type, WifiP2pDevice device) {

                        /*
                         * Searches for service of same type
                         */
                        if (service1.equalsIgnoreCase(myservice)) {
                            ServiceList fragment = (ServiceList) getFragmentManager()
                                    .findFragmentByTag("ESN");

                            /*
                             * If service is found, get the service/device details
                             * and add it to the array adapter
                             */
                            if (fragment != null) {
                                adapter = ((ServiceList.WiFiDevicesAdapter) fragment
                                        .getListAdapter());
                                P2PService service = new P2PService();
                                service.device = device;
                                service.serviceName = service1;
                                service.serviceType = reg_type;
                                adapter.add(service);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    /*
                     * Receives the actual description and connection information
                     * that is available
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                    }
                });

        /*
         * Create local service discovery request
         */
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int arg0) {

                    }
                });

         /*
          * Discovery all the available services
          */
        manager.discoverServices(channel,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int arg0) {
                    }
                });
    }

    /*
     * Connect to the respective service among the discovered services.
     * First get the information of the device through WifiP2pconfig
     *
     * @param service
     */
    @Override
    public void connectPeer(P2PService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        WifiP2pInfo p2pInfo;
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

        /*
         * Connect to a particular peer
         */
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            WifiP2pDevice device;

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int errorCode) {
            }
        });
    }

    /*
     * This is a Callback method to get the requested connection info.
     * After connecting to a peer discover if it is a groupowner or a peer
     *
     * @param p2pInfo
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;

        /*
         * If group is formed i.e a peer initiates connection,
         * create a socket for group owner
         */
        if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
            try {
                handler = new GOSocket(
                        ((ChatFragment.Message) this).getHandler());
                handler.start();
            } catch (IOException e) {
                return;
            }

        /*
         * If group is formed and a peer gets connection
         * request, then create a socket for client
         */
        } else if (p2pInfo.groupFormed){
            handler = new ClientSocket(
                    ((ChatFragment.Message) this).getHandler(),
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }
        chatFragment = new ChatFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.root, chatFragment).commit();
    }

    /*
     * Method to handle the message using handleMessage method of Handler
     * which allows the system to process the Runnable objects for the threads
     * and simultaneously displaying it on the chat screen
     *
     * @param msg
     * @return boolean
     */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case read:
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                (chatFragment).print("Peer: " + readMessage);
                break;
            case write:
                Object obj = msg.obj;
                (chatFragment).setChat((Chat) obj);
        }
        return true;
    }

    /*
     * Action to be performed when activity Pauses
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new MyBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intent);
    }

    /*
     * Action to be performed when activity Pauses
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /*
     * Action to be performed when activity Restarts
     */
    @Override
    protected void onRestart() {
        Fragment frag = getFragmentManager().findFragmentByTag("ESN");
        if (frag != null) {
            getFragmentManager().beginTransaction().remove(frag).commit();
        }
        super.onRestart();
    }

    /*
     * Action to be performed when activity Stops
     */
    @Override
    protected void onStop() {
        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onFailure(int reason) {

                }
                @Override
                public void onSuccess() {
                }
            });
        }
        super.onStop();
    }
}
