package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 8/4/15.
 */

import android.net.wifi.WifiManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/*
 * Class to broadcast the messages using address, port and
 * multicast lock
 */
public class MessageBroadcast {

    private WifiManager.MulticastLock lock;
    private WifiManager manager;
    private int port;
    private int len=1024;
    private Charset charset=Charset.forName("UTF-8");
    private boolean listening;
    private InetAddress m_addr;

    /**
     * Getters and Setters for the UDP packet information
     */
    public int getPacketLength() {
        return len;
    }
    public void setPacketLength(int packetLength) {
        this.len = packetLength;
    }
    public void setCharset(Charset charset) {
        this.charset = charset;
    }
    public void stopListen(){
        listening=false;
    }
    public void sendMulticast(String msg){
        send(m_addr,msg);
    }
    public Charset getCharset() {
        return charset;
    }
    public MessageBroadcast(WifiManager manager,int port) {
        this.lock = manager.createMulticastLock("ESNLock");
        this.manager=manager;
        this.port=port;
        try {
            this.m_addr=InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if WiFi connection is enabled
     *
     * @throws Exception
     */
    private void WiFiConnection() throws Exception
    {
        if(manager.isWifiEnabled()){
        }else{
            throw new Exception();
        }
    }

    /*
     * Starts listening to any received packets on the network
     * on the specified port number
     *
     * @param listener
     * @throws Exception
     */
    public void listen(final ReceiveListener listener) throws Exception {
        WiFiConnection();
        listening=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket datagramSocket;
                try {
                    datagramSocket = new DatagramSocket(port);
                    datagramSocket.setBroadcast(true);
                    byte[] data=new byte[len];

                    /*
                     * While in listening mode, it acquires the multicast lock when
                     * receives the packet. Upon receiving the packet, it releases
                     * the lock to receive further packets
                     */
                    while(listening){
                        DatagramPacket datagramPacket=new DatagramPacket(data,data.length);
                        try {
                            datagramSocket.receive(datagramPacket);
                            lock.acquire();
                            String msg= new String(datagramPacket.getData(),0,datagramPacket.getLength(),charset);
                            new ReceiveMessage(msg, datagramPacket.getAddress(), listener).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally{
                            lock.release();
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
     * Sends UDP packets to the given address
     *
     * @param address
     * @param msg
     */
    public void send(final InetAddress address,final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket s;
                try {
                    s = new DatagramSocket();
                    byte[] messageByte = msg.getBytes();
                    DatagramPacket p = new DatagramPacket(messageByte, messageByte.length, address, port);
                    s.send(p);
                    s.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                }catch( IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


