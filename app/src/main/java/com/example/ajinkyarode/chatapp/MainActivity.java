package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/16/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 *
 * Login activity for the application which includes EditTexts, Spinners
 * and Buttons to login to the application
 *
 */
public class MainActivity extends Activity {

    public static String uname;
    public static String ctr;
    private Button send;
    private int counter=3;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *
         * Spinner used for drop down menu items
         *
         */
        final Spinner sp = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.topics_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                int arg2, long arg3)
                {
                    ctr=sp.getSelectedItem().toString();
                }
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });

        /**
         *
         * Username, Password and Login Button details
         *
         */
        send = ( Button ) findViewById(R.id.btn_join);
        send.setOnClickListener(new View.OnClickListener() {
            EditText username = (EditText) findViewById(R.id.fld_username);
            EditText password = (EditText) findViewById(R.id.fld_password);
            @Override
            public void onClick(View v) {

                if(username.getText().toString().equals("Admin") ||username.getText().toString().equals("ESN")||username.getText().toString().equals("RIT") &&

                        password.getText().toString().equals("admin"))
                {
                    uname=username.getText().toString();
                    Intent intent = new Intent(MainActivity.this, MessageAll.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    counter--;
                    if (counter == 0) {
                        send.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}