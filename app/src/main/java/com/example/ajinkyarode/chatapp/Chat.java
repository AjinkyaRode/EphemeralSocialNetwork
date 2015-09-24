package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * Read/Write messages using Sockets
 */
public class Chat implements Runnable {
    private InputStream is;
    private OutputStream os;
    private Socket tcp_socket = null;
    private Handler handler;

    public Chat(Socket socket, Handler handler) {
        this.tcp_socket = socket;
        this.handler = handler;
    }

    /*
     * Send and receive messages using TCP sockets
     */
    @Override
    public void run() {
        try {
            is = tcp_socket.getInputStream();
            os = tcp_socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            handler.obtainMessage(DiscoveryActivity.write, this)
                    .sendToTarget();
            while (true) {
                try {
                    bytes = is.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    handler.obtainMessage(DiscoveryActivity.read,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                tcp_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            os.write(buffer);
        } catch (IOException e) {
        }
    }
}
