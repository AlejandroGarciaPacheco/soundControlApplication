package com.example.soundcontrolapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothHeadset;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioVolumeControl extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    Spinner languageDropdownMenu;
    ArrayAdapter myArrayAdapter;
    String selectedLanguage;

    SpeechRecognizer mySpeechRecognizer;

    VoiceControl myVoiceControl;

    private AudioManager audioManager;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;

    Map<Integer, Integer> identifyStream = new HashMap<>();
    TextView micText;
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
        setContentView(R.layout.activity_audio_volume_control);
        String [] myLanguages = {"English", "Spanish", "French", "Italian"};

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        radioGroup = findViewById(R.id.radioGroup);
        micText = findViewById(R.id.mic_text);
        ImageButton micBtn = (ImageButton)findViewById(R.id.microphone_button);

        identifyStream.put(R.id.radio_music, AudioManager.STREAM_MUSIC);
        identifyStream.put(R.id.radio_voicecall, AudioManager.STREAM_VOICE_CALL);
        identifyStream.put(R.id.radio_ring, AudioManager.STREAM_RING);
        identifyStream.put(R.id.radio_alarm, AudioManager.STREAM_ALARM);
        identifyStream.put(R.id.radio_notification, AudioManager.STREAM_NOTIFICATION);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioButton = findViewById(checkedId);
            }
        });



        //DROPDOWN MENU

        languageDropdownMenu = findViewById(R.id.spinner_language);
        myArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, myLanguages);
        languageDropdownMenu.setAdapter(myArrayAdapter);

        languageDropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = parent.getItemAtPosition(position).toString();
                Log.d("TAG", selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                String languageCode = "en-UK";

                if(selectedLanguage.toLowerCase().equals("english")){
                    languageCode = "en-UK";
                } else if (selectedLanguage.toLowerCase().equals("spanish")){
                    languageCode = "es-ES";
                } else if (selectedLanguage.toLowerCase().equals("french")){
                    languageCode = "fr-FR";
                } else if (selectedLanguage.toLowerCase().equals("italian")){
                    languageCode = "it-IT";
                }



                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SPEAK NOW");
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");

                try {

                    int radioId = selectedRadioButton.getId();
                    startForResult.launch(intent);
                    int selectedStream = identifyStream.getOrDefault(radioId, AudioManager.STREAM_MUSIC);

                    myVoiceControl = new VoiceControl(getApplicationContext(), selectedStream, languageCode);
                    myVoiceControl.startListening(AudioVolumeControl.this);


                } catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(), "You did not select a category", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}