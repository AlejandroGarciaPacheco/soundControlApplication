package com.example.soundcontrolapplication;

import android.content.Context;
import android.media.AudioManager;

public class VolumeClass {
    private int mediaVolume, voicecallVolume, ringVolume, notificationVolume, alarmVolume;
    String presetName;
    AudioManager myAudioManager;



    public VolumeClass(){

    }
    public VolumeClass(Context context, int mVolume, int vVolume, int rVolume, int aVolume, int nVolume){

        this.myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mediaVolume = mVolume;
        this.voicecallVolume = vVolume;
        this.ringVolume = rVolume;
        this.notificationVolume = nVolume;
        this.alarmVolume = aVolume;


    }
    public VolumeClass(int mVolume, int vVolume, int rVolume, int aVolume, int nVolume){

        this.mediaVolume = mVolume;
        this.voicecallVolume = vVolume;
        this.ringVolume = rVolume;
        this.notificationVolume = nVolume;
        this.alarmVolume = aVolume;


    }

    public void setVolume(){
        myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_SHOW_UI);
        myAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voicecallVolume, AudioManager.FLAG_SHOW_UI);
        myAudioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_SHOW_UI);
        myAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, AudioManager.FLAG_SHOW_UI);
        myAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationVolume, AudioManager.FLAG_SHOW_UI);

    }

    public int getMediaVolume() {
        return mediaVolume;
    }

    public void setMediaVolume(int mediaVolume) {
        this.mediaVolume = mediaVolume;
    }

    public int getVoicecallVolume() {
        return voicecallVolume;
    }

    public void setVoicecallVolume(int voicecallVolume) {
        this.voicecallVolume = voicecallVolume;
    }

    public int getRingVolume() {
        return ringVolume;
    }

    public void setRingVolume(int ringVolume) {
        this.ringVolume = ringVolume;
    }

    public int getNotificationVolume() {
        return notificationVolume;
    }

    public void setNotificationVolume(int notificationVolume) {
        this.notificationVolume = notificationVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }
}
