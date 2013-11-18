package com.utopia.bttendance.activity;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.R;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.helper.DeviceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Bttendance", "requestCode : " + requestCode + ", resultCode : " + resultCode);
        Toast.makeText(getApplicationContext(), "requestCode : " + requestCode + ", resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        ArrayAdapter<String> mAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button on_dialog = (Button) rootView.findViewById(R.id.blue_on_dialog);
            Button on = (Button) rootView.findViewById(R.id.blue_on);
            Button off = (Button) rootView.findViewById(R.id.blue_off);
            Button discover = (Button) rootView.findViewById(R.id.blue_discover);
            Button find = (Button) rootView.findViewById(R.id.blue_find);
            ListView list = (ListView) rootView.findViewById(R.id.blue_list);

            on_dialog.setOnClickListener(this);
            on.setOnClickListener(this);
            off.setOnClickListener(this);
            discover.setOnClickListener(this);
            find.setOnClickListener(this);

            mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
            list.setAdapter(mAdapter);

            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            getActivity().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

            Toast.makeText(getActivity(), DeviceHelper.getId(getActivity()) + ", " + DeviceHelper.getMacAddress(getActivity()), Toast.LENGTH_SHORT).show();

            return rootView;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            getActivity().unregisterReceiver(mReceiver);
        }

        // Create a BroadcastReceiver for ACTION_FOUND
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                // When discovery finds a devicebyte[] intentbyte = intent;
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ParcelUuid uuidp = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);

                    UUID uuid;

                    String intentlog =  intent.toString();
                    Log.e("Bttendance", intentlog);

                    byte[] intentbyte = null;

                    if(intentbyte!=null)
                        Log.e("Bttendance",intentbyte.toString());
                    else
                        Log.e("Bttendance", "intent is null");

                    if(uuidp != null){
                        uuid = uuidp.getUuid();
                        Log.e("Bttendance", uuid.toString());
                    }
                    // Add the name and address to an array adapter to show in a ListView

                    Log.e("Bttendance", device.getName() + "\n" + device.getAddress() + "\n" );
                    //Log.e("Bttendance", " " + device.getType());
                    //Log.e("Bttendance", ""+ device.getBluetoothClass().toString());
                    //Log.e("Bttendance", ""+ device.getBondState());
                    if (device == null) {
                        Log.e("Bttendance", "device is null");
                        mAdapter.add("device is null");
                    } else {
                        mAdapter.add(device.getName() + "\n" + device.getAddress() + "\n");
//
//                        if(device.getUuids()!=null){
//                            for (int i = 0; i < device.getUuids().length; i++) {
//                                Log.e("Bttendance", device.getUuids()[i].getUuid().toString());
//                                mAdapter.add(device.getUuids()[i].getUuid().toString());
//                            }
//                        }else{
//                            Log.e("Bttendance", "uuid is null");
//                            mAdapter.add("device.uuid is null");
//                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.blue_on_dialog:
                    BluetoothHelper.enable(getActivity());
                    return;
                case R.id.blue_on:
                    BluetoothHelper.enableWithUI();
                    return;
                case R.id.blue_off:
                    BluetoothHelper.disableWithUI();
                    return;
                case R.id.blue_discover:
                    BluetoothHelper.enableDiscoverability(getActivity());
                    return;
                case R.id.blue_find:
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Start Discover", Toast.LENGTH_SHORT).show();
                    BluetoothHelper.startDiscovery();
                    //BluetoothHelper.startLescan(BluetoothHelper.isAvailable());
                    return;
                default:
                    return;
            }
        }
    }


}
