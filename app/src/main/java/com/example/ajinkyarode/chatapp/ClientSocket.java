package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import java.net.Socket;
import android.os.Handler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/*
 * Client Socket to connect to the peer
 */
public class ClientSocket extends Thread {

    private Handler handler;
    private Chat chat;
    private InetAddress addr;

    public ClientSocket(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.addr = groupOwnerAddress;
    }

    public Chat getChat() {
        return chat;
    }

    /*
     * Connect to the peer using the address and port
     */
    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(addr.getHostAddress(),
                    DiscoveryActivity.port), 5000);
            chat = new Chat(socket, handler);
            new Thread(chat).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
}