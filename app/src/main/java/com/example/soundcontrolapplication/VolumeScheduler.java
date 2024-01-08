package com.example.soundcontrolapplication;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class VolumeScheduler extends AppCompatActivity {

    private CheckBox myMonCheckBox, myTueCheckBox, myWedCheckBox, myThuCheckBox, myFriCheckBox, mySatCheckBox, mySunCheckBox;
    private Button myScheduleAlarmButton, cancelButton;
    private TimePicker myTimePicker;
    private EditText mediaVolume, voicecallVolume, ringVolume, alarmVolume, notificationVolume;
    //private VolumeSchedulerReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_scheduler);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myScheduleAlarmButton = findViewById(R.id.fragment_createalarm_scheduleAlarm);
        myTimePicker = findViewById(R.id.fragment_createalarm_timePicker);
        cancelButton = findViewById(R.id.cancelVolumeScheduler);


        myTimePicker.setIs24HourView(true);

        mediaVolume = findViewById(R.id.mediaEditText);
        voicecallVolume = findViewById(R.id.voicecallEditText);
        ringVolume = findViewById(R.id.ringEditText);
        alarmVolume = findViewById(R.id.alarmEditText);
        notificationVolume = findViewById(R.id.notificationEditText);

        int hour = myTimePicker.getHour();
        int minute = myTimePicker.getMinute();
        myTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("TAG", "TIME CHANGED");
            }
        });

        myScheduleAlarmButton.setOnClickListener(new View.OnClickListener() {
            // Get the time selected by the user from the TimePicker
            int hour = myTimePicker.getHour();
            int minute = myTimePicker.getMinute();

            // Create a calendar instance and set the selected time
            Calendar calendar = Calendar.getInstance();

            @Override
            public void onClick(View v) {

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                if(mediaVolume.getText().toString().isEmpty()){
                    mediaVolume.setError("Media Volume cannot be empty");
                    mediaVolume.requestFocus();
                }

                else if(voicecallVolume.getText().toString().isEmpty()){
                    voicecallVolume.setError("Voicecall Volume cannot be empty");
                    voicecallVolume.requestFocus();
                }

                else if(ringVolume.getText().toString().isEmpty()){
                    ringVolume.setError("Ring Volume cannot be empty");
                    ringVolume.requestFocus();
                }

                else if(alarmVolume.getText().toString().isEmpty()){
                    alarmVolume.setError("Alarm Volume cannot be empty");
                    alarmVolume.requestFocus();
                }

                else if(notificationVolume.getText().toString().isEmpty()){
                    notificationVolume.setError("Notification Volume cannot be empty");
                    notificationVolume.requestFocus();

                } else {

                    // Schedule the alarm for the selected time and days

                    String volumeString = mediaVolume.getText().toString();
                    int mVolume = Integer.parseInt(volumeString);
                    String mVolumeString = voicecallVolume.getText().toString();
                    int voiceCVolume = Integer.parseInt(volumeString);
                    String rString = ringVolume.getText().toString();
                    int rVolume = Integer.parseInt(rString);
                    String aVolumeString = alarmVolume.getText().toString();
                    int aVolume = Integer.parseInt(aVolumeString);
                    String nVolumeString = notificationVolume.getText().toString();
                    int nVolume = Integer.parseInt(nVolumeString);

                    int mediaMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int mediaNewVolume = (mVolume * mediaMaxVolume)/ 100;


                    int voiceMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                    int voiceNewVolume = (voiceCVolume*voiceMaxVolume)/100;


                    int ringMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    int ringNewVolume = (rVolume*ringMaxVolume)/100;

                    int alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                    int alarmNewVolume = (aVolume*alarmMaxVolume)/100;

                    int notMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                    int notNewVolume = (nVolume*notMaxVolume)/100;

                    if (ringNewVolume==0){
                        ringNewVolume = 1;
                    }
                    if (voiceNewVolume == 0){
                        voiceNewVolume = 1;
                    }
                    if (notNewVolume < 1){
                        notNewVolume = 1;
                    }



                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    Intent intent = new Intent(getApplicationContext(), VolumeSchedulerReceiver.class);
                    intent.putExtra("uniqueRequestCode", 123);
                    intent.putExtra("mediaVolume", mediaNewVolume);
                    intent.putExtra("voicecallVolume", voiceNewVolume);
                    intent.putExtra("ringVolume", ringNewVolume);
                    intent.putExtra("alarmVolume", alarmNewVolume);
                    intent.putExtra("notificationVolume", notNewVolume);


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, FLAG_IMMUTABLE);


                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    getApplicationContext().sendBroadcast(intent);


                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VolumeSchedulerReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Log.d("TAG", "The alarm manager has been cancelled!");
                Toast.makeText(getApplicationContext(), "Your volume schedule has been cancelled!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("received_pending_intent");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the broadcast
            Log.d("TAG", "RECEIVED FINALLY");
        }
    };

}