package com.example.soundcontrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class preset_profile_4 extends AppCompatActivity {

    TextView mediaVolume, voicecallVolume, ringVolume, notificationVolume, alarmVolume, presetName;
    SeekBar mediaSB, voicecallSB, ringSB, notificationSB, alarmSB;
    Button saveButton;
    AudioManager myAudioManager;
    int mediaVolumeIndex, voicecallVolumeIndex, alarmVolumeIndex, notificationVolumeIndex = 0;
    int ringVolumeIndex = 1;
    DatabaseReference referenceProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_profile4);

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mediaVolume = findViewById(R.id.preset4_music_volume_index);
        voicecallVolume = findViewById(R.id.preset4_voice_call_volume_index);
        ringVolume = findViewById(R.id.preset4_ring_volume_index);
        notificationVolume = findViewById(R.id.preset4_notification_volume_index);
        alarmVolume = findViewById(R.id.preset4_alarm_volume_index);
        presetName = findViewById(R.id.text_preset_profile_4);

        mediaSB = findViewById(R.id.preset4_musicVolumeSB);
        voicecallSB = findViewById(R.id.preset4_voiceCallVolumeSB);
        ringSB = findViewById(R.id.preset4_ringVolumeSB);
        notificationSB = findViewById(R.id.preset4_notificationVolumeSB);
        alarmSB = findViewById(R.id.preset4_alarmVolumeSB);

        saveButton = findViewById(R.id.preset4_button);


        //MEDIA SEEKBAR
        mediaSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = (int) (progress / 100.0 * maxVolume);

                String mVolumeIndex = String.valueOf(volume);
                mediaVolume.setText(mVolumeIndex);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int progress = seekBar.getProgress();
                mediaVolumeIndex = (int) (progress/ 100.0 * maxVolume);
                Log.d("TAG", "VOLUME INDEX MEDIA " + mediaVolumeIndex);
            }
        });

        //VOICECALL SEEKBAR
        voicecallSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                int volume = (int) (progress / 100.0 * maxVolume);
                String vVolumeIndex = String.valueOf(volume);
                voicecallVolume.setText(vVolumeIndex);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                int progress = seekBar.getProgress();
                voicecallVolumeIndex = (int) (progress / 100.0 * maxVolume);
                Log.d("TAG", voicecallVolumeIndex+"");

            }
        });

        //RING SEEKBAR
        ringSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                int volume = (int) (progress / 100.0 * maxVolume);
                if (volume < 1){
                    volume = 1;
                }
                String rVolumeIndex = String.valueOf(volume);
                ringVolume.setText(rVolumeIndex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                int progress = seekBar.getProgress();
                ringVolumeIndex = (int) (progress / 100.0 * maxVolume);
                if (ringVolumeIndex < 1){
                    ringVolumeIndex = 1;
                }

                Log.d("TAG", ringVolumeIndex+"");
            }
        });

        //ALARM SEEKBAR

        alarmSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                int volume = (int) (progress / 100.0 * maxVolume);
                String aVolumeIndex = String.valueOf(volume);
                alarmVolume.setText(aVolumeIndex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                int progress = seekBar.getProgress();
                alarmVolumeIndex = (int) (progress / 100.0 * maxVolume);
                Log.d("TAG", alarmVolumeIndex+"");
            }
        });

        notificationSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                int volume = (int) (progress / 100.0 * maxVolume);
                if (volume < 1){
                    volume=1;
                }
                String nVolumeIndex = String.valueOf(volume);
                notificationVolume.setText(nVolumeIndex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                int progress = seekBar.getProgress();
                notificationVolumeIndex = (int) (progress / 100.0 * maxVolume);
                Log.d("TAG", notificationVolumeIndex+"");
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preset4call();
            }
        });


    }

    public void preset4call(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Preset Profiles");
        String preset4Name = presetName.getText().toString();
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean presetExists = false;
                if (snapshot.exists()){
                    Log.d("TAG", "EXISTS");
                    //we will retrieve values here

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        if (childSnapshot.hasChild(preset4Name)) {

                            String presetID = childSnapshot.getKey();
                            Log.d("TAG", presetID);
                            //WE HAVE OBTAINED THE PRESET ID NOW WE CAN UPDATE!
                            //USED TO CHECK WHERE IN THE DATAPATH I WAS LCOATED IN
                            //Log.d("TAG", String.valueOf(referenceProfile.getPath()));
                            VolumeClass myVolumeClassUpdate = new VolumeClass(mediaVolumeIndex, voicecallVolumeIndex, ringVolumeIndex, alarmVolumeIndex,notificationVolumeIndex);
                            // requires a HashMap to store the updated values.
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(presetID + "/" + preset4Name, myVolumeClassUpdate);
                            referenceProfile.updateChildren(childUpdates);

                            Log.d("TAG", "VALUES UPDATED");
                            presetExists = true;
                            break;
                        }

                    }
                }

                if (!presetExists) {
                    Log.d("TAG", "CREATING PRESET");
                    //creates a reference and sets default values
                    VolumeClass myVolumeClass = new VolumeClass(getApplicationContext(), mediaVolumeIndex, voicecallVolumeIndex, ringVolumeIndex, alarmVolumeIndex,notificationVolumeIndex);
                    String presetID = referenceProfile.push().getKey();
                    referenceProfile.child(presetID).child(preset4Name).setValue(myVolumeClass);
                    myVolumeClass.setVolume();
                }

                // remove the listener after it has done its job
                referenceProfile.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}