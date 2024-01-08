package com.example.soundcontrolapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class dashboardScreen extends AppCompatActivity {

    ImageView dashboardSliderImage;
    ImageView audio_volume_image;
    ImageView preset_image, scheduler_image;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        logoutButton = findViewById(R.id.logout_button);

        //VOLUME SLIDER ACTIVITY
        dashboardSliderImage = (ImageView) findViewById(R.id.sliderImage);
        dashboardSliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sliderIntent = new Intent(dashboardScreen.this, SliderVolume.class);
                startActivity(sliderIntent);
            }
        });

        //AUDIO VOLUME ACTIVITY
        audio_volume_image = (ImageView) findViewById(R.id.microphone_image);
        audio_volume_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sliderIntent = new Intent(dashboardScreen.this, AudioVolumeControl.class);
                startActivity(sliderIntent);
            }
        });

        //PRESET ACTIVITY
        preset_image = findViewById(R.id.preset_icon);
        preset_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sliderIntent = new Intent(dashboardScreen.this, PresetProfiles.class);
                startActivity(sliderIntent);
            }
        });

        //SCHEDULER ACTIVITY
        scheduler_image = findViewById(R.id.scheduler_icon);
        scheduler_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sliderIntent = new Intent(dashboardScreen.this, VolumeScheduler.class);
                startActivity(sliderIntent);
            }
        });

        //logout button

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Sign out the user
                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Create an intent to redirect to the login activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack and create a new task
                startActivity(intent); // Start the login activity
                finish(); // Finish the current activity to prevent the user from going back to it using the back button
            }
        });


    }
}