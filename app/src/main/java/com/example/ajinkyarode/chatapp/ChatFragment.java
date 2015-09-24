package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import java.util.ArrayList;
import java.util.List;

/*
 * Class to describe the chat window with all the elements
 * including textView, editText and Button
 */
public class ChatFragment extends Fragment {
    private TextView msg;
    private ListView list;
    private View view;
    private Chat chat;
    private List<String> items = new ArrayList<String>();
    ChatMessageAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        msg = (TextView) view.findViewById(R.id.txtChatLine);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1, items);
        list.setAdapter(adapter);
        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    /*
                     * Send the message when button is clicked
                     *
                     * @param arg0
                     */
                    @Override
                    public void onClick(View arg0) {
                        if (chat != null) {

                            /*
                             * Write the message to the peer
                             */
                            chat.write(msg.getText().toString()
                                    .getBytes());

                            /*
                             * Displays the message on current device
                             */
                            print("Me: " + msg.getText().toString());
                            msg.clearFocus();
                        }
                    }
                });
        return view;
    }

    public interface Message {
        public Handler getHandler();
    }
    public void setChat(Chat obj) {
        chat = obj;
    }

    /*
     * Displays the message in the TextView of the arraylist
     *
     * @param readMessage
     */
    public void print(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    /*
     * ArrayAdapter Class to manage chat messages in the list
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {
        //List<String> messages = null;
        public ChatMessageAdapter(Context context, int textViewResourceId,
                                  List<String> items) {
            super(context, textViewResourceId, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = convertView;
            if (view1 == null) {
                LayoutInflater li = (LayoutInflater) getActivity()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                view1 = li.inflate(android.R.layout.simple_list_item_1, null);
            }

            /*
             * Set the position of the text to be dislplayed in the list
             */
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                TextView txt_msg = (TextView) view1
                        .findViewById(android.R.id.text1);
                if (txt_msg != null) {
                    txt_msg.setText(message);
                    txt_msg.setBackgroundColor(Color.WHITE);
                    txt_msg.setTextAppearance(getActivity(),
                            R.style.normalText);
                }
            }
            return view1;
        }
    }
}