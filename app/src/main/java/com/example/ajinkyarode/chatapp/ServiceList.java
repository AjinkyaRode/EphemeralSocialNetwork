package com.example.ajinkyarode.chatapp;

/**
 * Created by ajinkyarode on 7/20/15.
 */

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/*
 * ListFragment to display the list of discovered services
 * that are added by other devices/peers on the network
 */
public class ServiceList extends ListFragment {
    WiFiDevicesAdapter list = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new WiFiDevicesAdapter(this.getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1,
                new ArrayList<P2PService>());
        setListAdapter(list);
    }

    /*
     * Perform connection with the selected/clicked peer
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((DeviceClickListener) getActivity()).connectPeer((P2PService) l
                .getItemAtPosition(position));
    }

    public class WiFiDevicesAdapter extends ArrayAdapter<P2PService> {
        private List<P2PService> items;
        public WiFiDevicesAdapter(Context context, int resource,
                                  int textViewResourceId, List<P2PService> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        /*
         * Display the device names in the list of discovered peers
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = convertView;
            if (view1 == null) {
                LayoutInflater li = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view1 = li.inflate(android.R.layout.simple_list_item_2, null);
            }
            P2PService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) view1
                        .findViewById(android.R.id.text1);
                if (nameText != null) {
                    nameText.setText(service.device.deviceName);
                }
            }
            return view1;
        }
    }

    /*
     * Interface with connectPeer that is defined in DiscoverActivity
     */
    interface DeviceClickListener {
        public void connectPeer(P2PService service1);
    }

}
