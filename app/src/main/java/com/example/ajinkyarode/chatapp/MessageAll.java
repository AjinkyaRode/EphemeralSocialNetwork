package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 8/4/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.InetAddress;

/*
 * Class to handle the multicast messages. It uses TextView, EditText and Button
 * to send/receive messages on the Activity window
 */
public class MessageAll extends Activity implements View.OnClickListener, ReceiveListener {

    private TextView msg_rec;
    private EditText msg_snd;
    private MessageBroadcast msg_broadcast;
    private Button broadcast;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_message_window);
        msg_rec = (TextView) findViewById(R.id.msg_receive);
        msg_snd = (EditText) findViewById(R.id.msg_send);

        /*
         * Broadcasts the message to all the peers on click event of the Send button
         */
        broadcast = (Button) findViewById(R.id.btnSend);
        broadcast.setOnClickListener(this);
        msg_broadcast=new MessageBroadcast((WifiManager) getSystemService(WIFI_SERVICE), 8001);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                try {
                    msg_broadcast.listen(MessageAll.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*
         * Navigates to service discovery activity to connect to a peer
         */
        connect = ( Button ) findViewById(R.id.btnConnect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageAll.this, DiscoveryActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
     * Prints the messages on the message window
     *
     * @param message
     */
    public void print(final String message){
        msg_rec.post(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder(msg_rec.getText().toString());
                sb.insert(0, "User: " + message + "\n");
                msg_rec.setText(sb.toString());
            }
        });
    }

    /*
     * Sends the respective message on Button click event
     *
     * @param arg0
     */
    @Override
    public void onClick(View arg0) {
        msg_broadcast.sendMulticast(msg_snd.getText().toString());
        msg_snd.setText("");
    }

    /*
     * Receives and displays messages
     *
     * @param arg0
     */
    @Override
    public void receive(InetAddress address, String msg) {
        print(msg);
    }
}