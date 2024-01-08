package com.example.soundcontrolapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;


public class SliderVolume extends AppCompatActivity {
    //SEEKBARS
    SeekBar musicVolume;
    SeekBar notificationVolume;
    SeekBar alarmVolume;
    SeekBar ringVolume;
    SeekBar voiceCall;

    //TEXT VIEWS
    TextView mVolumeIndex;
    TextView notificationVolumeIndex;
    TextView alarmVolumeIndex;
    TextView ringVolumeIndex;
    TextView voiceCallIndex;

    private BluetoothService mBluetoothService;
    private boolean mBound = false;

    //AUDIO MANAGER CLASS VARIABLE DECLARATIONS
    AudioManager myAudioManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_volume);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);



        //SERVICE CONNECTION
        Intent intent = new Intent(this, BluetoothService.class);

        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        //MUSIC/MEDIA VOLUME SEEKBAR

        musicVolume = findViewById(R.id.musicVolumeSB);
        mVolumeIndex = findViewById(R.id.music_volume_index);


        musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = (int) (progress / 100.0 * maxVolume);
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
                Log.d("TAG", "Volume is at: " + volume);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }
        });

        //VOICE CALL VOLUME SEEKBAR
        voiceCall = findViewById(R.id.voiceCallVolumeSB);
        voiceCallIndex = findViewById(R.id.voice_call_volume_index);
        voiceCall.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                myAudioManager.setMode(AudioManager.MODE_IN_CALL);
                myAudioManager.startBluetoothSco();

                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                int volume = (int) (progress / 100.0 * maxVolume);
                myAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, AudioManager.FLAG_SHOW_UI);

                Log.d("TAG", "Volume is at: " + volume);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                voiceCallIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                voiceCallIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }
        });

        //RINGTONE SEEKBAR


        ringVolume = findViewById(R.id.ringVolumeSB);

        ringVolumeIndex = findViewById(R.id.ring_volume_index);

        ringVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //DOES NOT ALLOW TO ENTER IN DO NOT DISTURB MODE OR SILENT MODE
                //SO I SET A SEEKBAR TO MINIMUM OF 1 AND MADE AN IF STATEMENT TO PREVENT IT FROM CRASHING.
                seekBar.setMin(1);
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                int volume = (int) (progress / 100.0 * maxVolume);
                if (volume == 0){
                    volume = 1;
                    myAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
                }
                myAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
                Log.d("TAG", "Volume is at: " + volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                ringVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                //seekBar.setMin(1);
                ringVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }
        });

        //ALARM SEEKBAR

        alarmVolume = findViewById(R.id.alarmVolumeSB);
        alarmVolumeIndex = findViewById(R.id.alarm_volume_index);

        alarmVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                int volume = (int) (progress / 100.0 * maxVolume);
                myAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_SHOW_UI);
                Log.d("TAG", "Volume is at: " + volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                alarmVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                alarmVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }
        });





        //NOTIFICATION SEEKBAR
        notificationVolume = findViewById(R.id.notificationVolumeSB);
        notificationVolumeIndex = findViewById(R.id.notification_volume_index);

        notificationVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                int volume = (int) (progress / 100.0 * maxVolume);
                if (volume < 1){
                    volume = 1;
                }
                myAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, AudioManager.FLAG_SHOW_UI);
                Log.d("TAG", "Volume is at: " + volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                notificationVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                notificationVolumeIndex.setText("Volume: " + volume);
                Log.d("TAG", "headset volume: " + volume);
            }
        });




    }

    //-----------------SET UP LOCAL BROADCAST AND SERVICE CONNECTION------------------------------------------------
    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);

                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    // Headset is connected
                    //setMusicVolume();
                    Log.d("TAG", "HEADSET CONNECTED");

                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    // Headset is disconnected
                    Log.d("TAG", "HEADSET DISCONNECTED");
                    Toast.makeText(getApplicationContext(), "Headset disconnected, functionalities disabled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    public void onPause(){
        super.onPause();
        //unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unbindService(mServiceConnection);
        mBound = false;
    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) service;
            mBluetoothService = binder.getService();
            mBound = true;
            Log.d("TAG", "SERVICE CONNECTED IN SLIDERVOLUME SCREEN");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
            mBound = false;
        }

    };
}