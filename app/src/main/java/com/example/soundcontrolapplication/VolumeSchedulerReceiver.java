package com.example.soundcontrolapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class VolumeSchedulerReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "ITS WORKING");

        int mediaVolume = intent.getIntExtra("mediaVolume", 0);
        int voicecallVolume = intent.getIntExtra("voicecallVolume", 1);
        int ringVolume = intent.getIntExtra("ringVolume", 1);
        int alarmVolume = intent.getIntExtra("alarmVolume", 0);
        int notificationVolume = intent.getIntExtra("notificationVolume", 0);

        VolumeClass myVolumeClass = new VolumeClass(context, mediaVolume, voicecallVolume, ringVolume, alarmVolume, notificationVolume);
        myVolumeClass.setVolume();


    }



}
