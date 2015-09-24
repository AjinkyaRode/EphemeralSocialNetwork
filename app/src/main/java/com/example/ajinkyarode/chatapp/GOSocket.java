package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.os.Handler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * ServerSocket used by the Group Owner to create new
 * serversocket and connect to the peers
 */
public class GOSocket extends Thread {
    ServerSocket socket = null;
    private final int threads = 10;
    private Handler handler;

    /**
     * Create a ServerSocket
     *
     * @param handler
     * @throws IOException
     */
    public GOSocket(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(4545);
            this.handler = handler;
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }

    /*
     * Create a pool of threads to handle multiple peer requests
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            threads, threads, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    /*
     * Start the server and add peers to it
     */
    @Override
    public void run() {
        while (true) {
            try {
                pool.execute(new Chat(socket.accept(), handler));
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {
                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }
}