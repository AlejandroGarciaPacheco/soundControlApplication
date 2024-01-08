package com.example.soundcontrolapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.VolumeProvider;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BluetoothService extends Service {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final int START_DISCOVERY = 1;
    Set<BluetoothDevice> noDupDevices = new HashSet<>();
    ArrayList<BluetoothDevice> myDevices;
    private ConnectThread myThread;

    BluetoothManager myBluetoothManager;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice device;
    BluetoothHeadset myHeadsetDevice;


    //ArrayAdapter arrayAdapter;
    private boolean isScanning;


    //PERMISSIONS
    private static final String[] android13Permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };
    private static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private static final String[] android12Permissions = {

    };
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;



    @Override
    public void onCreate() {
        super.onCreate();

        myBluetoothManager = getSystemService(BluetoothManager.class);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //Activity activity = (Activity) intent.getExtras().get("activity");
        if (myBluetoothAdapter!=null){
            if (myBluetoothAdapter.isEnabled()){
                // Register the Bluetooth discovery receiver
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothDevice.ACTION_FOUND);

                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
                registerReceiver(myBroadcastReceiver, filter);
            }
        }



        return START_NOT_STICKY;
    }

    public void turnBluetoothOn(Activity activity){
        //Log.d("TAG", "IT WORKS");
        if (myBluetoothAdapter == null){
            //Bluetooth device not supported
            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!myBluetoothAdapter.isEnabled()){

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Bluetooth permissions for Android above or equal 13
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(enableBluetoothIntent);
                    //myBluetoothAdapter.enable();

                    Log.d("TAG", "Bluetooth ON");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Bluetooth permissions for Android  12
                Log.d("TAG", "THIS IS FOR ANDROID 12");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(enableBluetoothIntent);

                    Log.d("TAG", "Bluetooth OFF");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    Manifest.permission.BLUETOOTH_CONNECT

                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }

            else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                // Bluetooth permissions for Android below 12
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(enableBluetoothIntent);
                    //myBluetoothAdapter.enable();

                    Log.d("TAG", "Bluetooth ON");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }



        }
    }

    public void turnBluetoothOff(Activity activity){
        if (myBluetoothAdapter == null){
            //Bluetooth device not supported
            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(myBluetoothAdapter.isEnabled()){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Bluetooth permissions for Android below 12
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    Intent disableBluetoothIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    disableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(disableBluetoothIntent);

                    Log.d("TAG", "Bluetooth OFF");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions(activity,
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }
            else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // Bluetooth permissions for Android below 12
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    myBluetoothAdapter.disable();

                    Log.d("TAG", "Bluetooth OFF");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }


        }
    }

    public void startScanning(){
        if (isScanning) {
            stopScanning();
            Log.d("TAG", "SCAN CANCELLED");
        }
        //myDevices.clear();
        //arrayAdapter.notifyDataSetChanged();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Bluetooth permissions for Android 12 and later
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth permissions granted
                // Start scanning for devices here
                myBluetoothAdapter.startDiscovery();
                isScanning = true;
                Log.d("TAG", "SCANNING HERE TOO");

                Log.d("TAG", "IT WORKED");
                //setDeviceName();
                //arrayAdapter.notifyDataSetChanged();

            } else {
                // Bluetooth permissions not granted
                // Request Bluetooth permissions from the user
                ActivityCompat.requestPermissions((Activity)getApplicationContext(),
                        new String[]{
                                android.Manifest.permission.BLUETOOTH_SCAN,
                                android.Manifest.permission.BLUETOOTH_CONNECT
                        },
                        REQUEST_BLUETOOTH_PERMISSIONS);
            }

        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            // Bluetooth permissions for Android 12
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth permissions granted
                // Start scanning for devices here
                myBluetoothAdapter.startDiscovery();
                isScanning = true;
                Log.d("TAG", "SCANNING HERE TOO");

                Log.d("TAG", "IT WORKED");
                //setDeviceName();
                //arrayAdapter.notifyDataSetChanged();

            } else {
                // Bluetooth permissions not granted
                // Request Bluetooth permissions from the user
                ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN

                        },
                        REQUEST_BLUETOOTH_PERMISSIONS);
            }

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (checkSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)getApplicationContext(),
                        new String[]{
                                android.Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                // Permissions already granted, start discovery
                Log.d("TAG", "I AM EXECUTING 1");
                myBluetoothAdapter.startDiscovery();
                isScanning = true;


            }
        }
    }

    public void stopScanning() {
        if (isScanning) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Bluetooth permissions for Android 12 and later
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permissions granted
                    // Start scanning for devices here
                    myBluetoothAdapter.cancelDiscovery();
                    Log.d("TAG", "DISCOVERY CANCELLED");
                } else {
                    // Bluetooth permissions not granted
                    // Request Bluetooth permissions from the user
                    ActivityCompat.requestPermissions((Activity)getApplicationContext(),
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH_SCAN,
                                    android.Manifest.permission.BLUETOOTH_CONNECT
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                }

            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)getApplicationContext(),
                            new String[]{
                                    android.Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                } else {
                    // Permissions already granted, start discovery
                    myBluetoothAdapter.cancelDiscovery();
                    Log.d("TAG", "DISCOVERY CANCELLED");

                }
            }

        }
    }

    public void transferContent(){
        myDevices = new ArrayList<>(noDupDevices);
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Activity activity = (Activity) intent.getExtras().get("activity");
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // Discovery started
                Log.d("TAG", "DISCOVERY STARTED");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Discovery finished

                transferContent();
                Log.d("TAG", "SIZE =  " + myDevices.size());
                Intent sendListIntent = new Intent("device_list");
                sendListIntent.putParcelableArrayListExtra("deviceList", myDevices);
                sendBroadcast(sendListIntent);
                noDupDevices.clear();
                myDevices.clear();

                Log.d("TAG", "DISCOVERY FINISHED");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Device found
                Log.d("TAG", "DEVICE FOUND");
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                noDupDevices.add(device);

            } else if (BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                // Device name changed
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);

                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    // Headset is connected
                    setMusicVolume();
                    Log.d("TAG", "HEADSET CONNECTED");

                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    // Headset is disconnected
                    Log.d("TAG", "HEADSET DISCONNECTED");
                }
            }
        }
    };

    public void setMusicVolume(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        /*
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        AudioDeviceInfo deviceConnected = null;
        //BluetoothA2dp a2dp;
        Log.d("TAG", "I AM STILL CONNECTED ");
        for (AudioDeviceInfo device : audioDevices) {
            if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP){
                deviceConnected = device;
                break;
            }

        }

         */



        //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();
        //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("TAG", "Earbuds volume in broadcast: " + volume);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "DESTROYED");

        // Unregister the BroadcastReceiver
        unregisterReceiver(myBroadcastReceiver);


    }
    /*
    public void startingThreadConnection(BluetoothDevice selectedDevice){
        ConnectThread connectMyThread = new ConnectThread();
        connectMyThread.execute(selectedDevice);
    }

     */

    public void activateThread(BluetoothDevice theDevice){
        myThread = new ConnectThread(theDevice);
        myThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice myDevice;
        BluetoothHeadset myBluetoothHeadset;
        AudioManager myAudioManager;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            myDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("TAG", "THis is android 12 and 13");
                if (checkSelfPermission(PERMISSIONS[3]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    Manifest.permission.BLUETOOTH_CONNECT
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                } else {


                    try {
                        tmp = myDevice.createRfcommSocketToServiceRecord(myUUID);
                    } catch (IOException e) {
                        Log.e("TAG", "Socket's create() method failed", e);
                    }


                }
            }
            mmSocket = tmp;
        }


        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            //myBluetoothAdapter.cancelDiscovery();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("TAG", "THis is android 1333334444");
                if (checkSelfPermission(PERMISSIONS[3]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{
                                    Manifest.permission.BLUETOOTH_CONNECT
                            },
                            REQUEST_BLUETOOTH_PERMISSIONS);
                } else {


                    ParcelUuid[] uuids = myDevice.getUuids();
                    if (uuids != null) {
                        // Display the UUID(s) in the logcat
                        for (ParcelUuid uuid : uuids) {
                            Log.d("Bluetooth UUID", uuid.toString());
                            if (uuid.getUuid().equals(myUUID)) {

                            }
                        }
                    }

                    try {
                        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                            device.createBond();
                            Log.d("TAG", "THE GET BOND WORKED");

                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Error pairing with device through getBond: " + e.getMessage());

                    }


                    try {
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        mmSocket.connect();
                        Log.d("TAG", "CONNECTED");



                    } catch (IOException connectException) {
                        // Unable to connect; close the socket and return.
                        Log.d("TAG", "UNABLE TO CONNECT " + connectException);
                        try {
                            mmSocket.close();
                        } catch (IOException closeException) {
                            Log.e("TAG", "Could not close the client socket", closeException);
                        }



                    }

                }
            }
        }


    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothBinder();
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }


}



