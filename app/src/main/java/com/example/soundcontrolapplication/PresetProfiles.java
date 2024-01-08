package com.example.soundcontrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PresetProfiles extends AppCompatActivity {
    private TextView preset_tv_1, preset_tv_2, preset_tv_3, preset_tv_4;
    private ImageButton ib_arrow_1, ib_arrow_2, ib_arrow_3, ib_arrow_4;
    DatabaseReference referenceProfile;
    String presetName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_profiles);

        preset_tv_1 = findViewById(R.id.preset1);
        preset_tv_2 = findViewById(R.id.preset2);
        preset_tv_3 = findViewById(R.id.preset3);
        preset_tv_4 = findViewById(R.id.preset4);

        ib_arrow_1 = findViewById(R.id.preset_button_1);
        ib_arrow_2 = findViewById(R.id.preset_button_2);
        ib_arrow_3 = findViewById(R.id.preset_button_3);
        ib_arrow_4 = findViewById(R.id.preset_button_4);


        //TEXT VIEW PRESET1
        preset_tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetCall("Preset1");
            }
        });
        preset_tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetCall("Preset2");
            }
        });

        preset_tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetCall("Preset3");
            }
        });

        preset_tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetCall("Preset4");
            }
        });

        //PRESET 1
        ib_arrow_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PresetProfiles.this, preset_profile_1.class);
                startActivity(intent);
            }
        });

        //PRESET2
        ib_arrow_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PresetProfiles.this, preset_profile_2.class);
                startActivity(intent);
            }
        });

        //PRESET3
        ib_arrow_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PresetProfiles.this, preset_profile_3.class);
                startActivity(intent);
            }
        });

        //PRESET4
        ib_arrow_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PresetProfiles.this, preset_profile_4.class);
                startActivity(intent);
            }
        });

    }

    public void presetCall(String pName){


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Preset Profiles");

        if (pName.equals("Preset1")) {
            presetName = preset_tv_1.getText().toString();
        } else if (pName.equals("Preset2")) {
            presetName = preset_tv_2.getText().toString();
        } else if (pName.equals("Preset3")) {
            presetName = preset_tv_3.getText().toString();
        } else if (pName.equals("Preset4")) {
            presetName = preset_tv_4.getText().toString();
        }


        Log.d("TAG", presetName);

        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean presetExists = false;
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.hasChild(presetName)) {
                            String presetID = childSnapshot.getKey();
                            Log.d("TAG", presetID);
                            //WE HAVE OBTAINED THE PRESET ID NOW WE CAN RETRIEVE!
                            referenceProfile.child(presetID).child(presetName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    VolumeClass myVolumeClass = snapshot.getValue(VolumeClass.class);
                                    int media_volume = myVolumeClass.getMediaVolume();
                                    int voicecall_volume = myVolumeClass.getVoicecallVolume();
                                    int ring_volume = myVolumeClass.getRingVolume();
                                    int alarm_volume = myVolumeClass.getAlarmVolume();
                                    int notification = myVolumeClass.getNotificationVolume();

                                    VolumeClass thisSetsVolumeClass = new VolumeClass(getApplicationContext(), media_volume, voicecall_volume, ring_volume, alarm_volume, notification);
                                    thisSetsVolumeClass.setVolume();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // handle error
                                }
                            });
                            presetExists = true;
                            break;
                        }
                    }
                }
                if (!presetExists) {
                    Log.d("TAG", "CREATING PRESET");
                    //creates a reference and sets default values
                    VolumeClass myVolumeClass = new VolumeClass(getApplicationContext(), 5,2,3,3,3);
                    String presetID = referenceProfile.push().getKey();
                    referenceProfile.child(presetID).child(presetName).setValue(myVolumeClass);
                    myVolumeClass.setVolume();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle error
            }
        });


    }
}