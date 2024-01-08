package com.example.soundcontrolapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class bluetoothScreen extends AppCompatActivity {
    //public static final String EXTRA_ACTIVITY = "com.example.myapp.EXTRA_ACTIVITY";
    Button dashboardButton;
    Switch bluetoothSwitchState;
    Button scanButton;
    ListView deviceList;
    Set<BluetoothDevice> noDupDevices = new HashSet<>();



    BluetoothAdapter myBluetoothAdapter;
    BluetoothManager myBluetoothManager;
    BluetoothDevice device;

    Intent enableBTIntent;
    //UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final String[] android13Permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN
    };
    private static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    ArrayList<BluetoothDevice> myListOfDevices;

    ArrayList<String> myDevices = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    private boolean isScanning;
    private Handler myHandler = new Handler();
    //CONNECTION SERVICE VARIABLES
    private BluetoothService mBluetoothService;
    private boolean mBound = false;


    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {

            }
        }

    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_screen);
        bluetoothSwitchState = (Switch) findViewById(R.id.switch2);
        scanButton = (Button) findViewById(R.id.scan_button);
        deviceList = (ListView) findViewById(R.id.list_devices_textArea);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);





        //SERVICE CONNECTION
        Intent intent = new Intent(this, BluetoothService.class);

        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);





        //DASHBOARD BUTTON
        dashboardButton = (Button) findViewById(R.id.bdashboard);

        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bluetoothScreen.this, dashboardScreen.class);
                startActivity(intent);
            }
        });


        //SCAN BUTTON
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound){
                    Log.d("TAG", "IN SCAN BUTTON: " + mBound);
                    myDevices.clear();
                    arrayAdapter.notifyDataSetChanged();
                    mBluetoothService.startScanning();
                    Log.d("TAG", "SCAN STARTED IN ACTIVITY");

                }

            }
        });


        //Log.d("TAG", "RETRIEVING THIS NUMBER OF DEVICES: "+ myListOfDevices.size());

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, myDevices);
        deviceList = findViewById(R.id.list_devices_textArea);
        deviceList.setAdapter(arrayAdapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDevice = myDevices.get(position);

                if (mBound) {
                    BluetoothDevice theDevice = connectToDevice(selectedDevice);
                    mBluetoothService.activateThread(theDevice);
                    //mBluetoothService.startingThreadConnection(theDevice);

                }


            }
        });

        //SWITCH
        bluetoothSwitchState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startForResult.launch(enableBTIntent);
                if (mBound){
                    Log.d("TAG", "IN SWITCH: " + mBound);
                    if (bluetoothSwitchState.isChecked()) {
                        mBluetoothService.turnBluetoothOn(bluetoothScreen.this);
                    } else {
                        mBluetoothService.turnBluetoothOff(bluetoothScreen.this);
                    }

                }


            }
        });

    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                Log.d("TAG", "DISCOVERY STARTED");
                // Discovery started
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Discovery finished

                Log.d("TAG", "DISCOVERY FINISHED");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Device found
                Log.d("TAG", "DEVICE FOUND");
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                noDupDevices.add(device);

            } else if (BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                // Device name changed
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            } else if ("device_list".equals(action)){
                //Receives data array list, clears everything stored in my devices and notifies that change to the array adapter
                //to allow it to populate new devices discovered after the scan button is pressed
                myDevices.clear();
                arrayAdapter.notifyDataSetChanged();
                myListOfDevices = intent.getParcelableArrayListExtra("deviceList");
                setDeviceName();
                arrayAdapter.notifyDataSetChanged();
                Log.d("TAG", "SIZE OF THE ARRAY: " + myDevices.size());

            }
        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver
        unregisterReceiver(myBroadcastReceiver);
        unbindService(mServiceConnection);
        mBound = false;
    }


    @Override
    protected void onResume() {
        super.onResume();


        // Register the Bluetooth discovery receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction("device_list");
        //filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the Bluetooth discovery receiver
        //unregisterReceiver(myBroadcastReceiver);
    }

    public BluetoothDevice connectToDevice(String deviceName) {
        BluetoothDevice selectedDevice = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            Log.d("TAG", "THis is android 12 or above");
            if (checkSelfPermission(android13Permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android13Permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android13Permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(android13Permissions, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                //ACESS GRANTED
                Log.d("TAG", "ACCESS GRANTED");
                for (BluetoothDevice d : noDupDevices) {
                    if (d.getName()!=null) {
                        if (d.getName().equals(deviceName)) {
                            selectedDevice = d;
                            Log.d("TAG", "SELECTED DEVICE: "+ selectedDevice.getName());
                            Log.d("TAG", "DEVICE IDENTIFIED");
                            return selectedDevice;


                        }
                    }


                }
            }

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            //mobile versions below 12
            Log.d("TAG", "This is android below 12");
        }
        return null;

    }

    public void setDeviceName() {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (checkSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                // read device
                for (BluetoothDevice d : myListOfDevices) {
                    String name = d.getName();
                    //Log.d("TAG", name);
                    if (name != null) {
                        myDevices.add(name);
                    }

                }


            }
        }

        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (checkSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                // read device
                for (BluetoothDevice d : myListOfDevices) {
                    String name = d.getName();
                    if (name == null) {
                        name = d.getAddress();
                    }
                    myDevices.add(name);
                }
                //myDevices.add(newDevice);
                Log.d("TAG", "COUNT: " + myDevices.size());


            }
        }


    }

        //-----------------THIS IS FOR THE SERVICE CONNECTION-------------------------------
        private ServiceConnection mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) service;
                mBluetoothService = binder.getService();
                mBound = true;
                Log.d("TAG", "SERVICE CONNECTED");
                Log.d("TAG", "SERVICE: " + mBound);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBluetoothService = null;
                mBound = false;
            }

    };

}



