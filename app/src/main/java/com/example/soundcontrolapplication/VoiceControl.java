package com.example.soundcontrolapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoiceControl implements RecognitionListener {

    private Context context;
    private AudioManager audioManager;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    String languageSelected;
    String languageCode;


    int streamType;
    int REQUEST_AUDIO_PERMISSIONS = 1;

    Boolean isTranslationComplete = false;
    String[] translatedWord = new String[1];

    //Pattern pattern = Pattern.compile("(increase|increment|raise|turn up|decrease|decrement|lower|turn down|add|reduce)\\s+(the\\s+)?(volume)\\s+(by\\s+)?((one|two|three|four|five|six|seven|eight|nine)|(\\d+))");
    Pattern pattern = Pattern.compile("(increase|increment|raise|turn up|decrease|decrement|lower|turn down|add|reduce)\\s+(the\\s+)?(volume)\\s*(by|for|to|of)?\\s*((one|two|three|four|five|six|seven|eight|nine)|(\\d+))");


    public VoiceControl(Context context, int streamType, String languageSelected){
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        this.textToSpeech = new TextToSpeech(context, null);
        this.streamType = streamType;
        this.languageSelected = languageSelected;

    }


    //OR PASS IT IN THE CONSTRUCTOR


    public String translateText(String sourceLang, String targetLang, String text){

        HashMap<String, TranslatorOptions> translatorOptionsHashMap = new HashMap<>();
        translatorOptionsHashMap.put("fr", new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.FRENCH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build());
        translatorOptionsHashMap.put("es", new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build());

        translatorOptionsHashMap.put("it", new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ITALIAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build());

        ArrayList<String> myArray = new ArrayList<>();
        /*
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.FRENCH)
                .setTargetLanguage(TranslateLanguage.ENGLISH).build();

         */

        TranslatorOptions myOption = translatorOptionsHashMap.get(sourceLang);

        Translator translator = Translation.getClient(myOption);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi().build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Translation Model Ready to translate
                        Log.d("TAG", "DOWNLOAD SUCCESSFUL");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(context, "Unsuccessful download due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        translator.translate(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedWord[0] = s;
                        isTranslationComplete = true;
                        Log.d("TAG", "MY WORD ISSSSSS: " + translatedWord[0]);
                        testingFunction(s);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        Log.d("TAG", "My word is still " + translatedWord[0]);
        return translatedWord[0];

    }




    public void startListening(Activity activity){
        /*
        if (languageSelected.toLowerCase().equals("english")){
            Log.d("TAG", "LANGUAGE IS ENGLISH");
            languageCode = "en-UK";
        } else if (languageSelected.toLowerCase().equals("spanish")){
            Log.d("TAG", "LANGUAGE IS SPANISH");
            languageCode="es-ES";
        } else if (languageSelected.toLowerCase().equals("french")){
            languageCode = "fr-FR";
        }

         */



        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageSelected);
        speechRecognizer.startListening(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    REQUEST_AUDIO_PERMISSIONS);
        }

    }

    public void stopListening(){
        speechRecognizer.stopListening();
        this.textToSpeech.stop();
    }
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("TAG", "READY");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("TAG", "Beginning of speech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("TAG", "Speech ended");
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        String wordTranslated = "";
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //Pattern pattern = Pattern.compile("(increase|increment|raise|turn up|decrease|decrement|lower|turn down)\\s+(the\\s+)?(volume)\\s+(by\\s+)?((one|two|three|four|five|six|seven|eight|nine)|(\\d+))");

        HashMap<String, Integer> numbers = new HashMap<>();
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);

        String text = matches.get(0);
        Matcher matcher = pattern.matcher(text.toLowerCase());
        Matcher translatedMatcher = null;
        Log.d("TAG", text);


        ArrayList<String> matchingWords = new ArrayList<>();
        matchingWords.add("increase volume by ");
        matchingWords.add("raise volume by ");
        matchingWords.add("turn-up volume by ");

        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        int currentVolume = audioManager.getStreamVolume(streamType);
        int volumeToIncrease = 0;
        int volumeToDecrease = 0;
        String volumeIndex = "";
        int newVolume;

        Log.d("TAG", "MY LANGUAGE CODE IS AS FOLLOW: " + languageSelected.substring(0,2));

        //String myWord = translateText("fr", "en", "bonjour");


        if (!languageSelected.substring(0, 2).equals("en")) {
            wordTranslated = translateText(languageSelected.substring(0, 2), "en", text);
            //translatedMatcher = pattern.matcher(wordTranslated.toLowerCase());

        }


        if (matcher.find()){
            Log.d("TAG", "FOUND!");

            if (matcher.group(1).equals("increase") || (matcher.group(1).equals("increment"))  || (matcher.group(1).equals("raise")) || (matcher.group(1).equals("turn up")) || matcher.group(1).equals("add")){
                if(matcher.group(5).equals("one")){
                    volumeToIncrease = 1;
                } else if (matcher.group(5).equals("two")){
                    volumeToIncrease = 2;
                } else if (matcher.group(5).equals("three")) {
                    volumeToIncrease = 3;
                } else if (matcher.group(5).equals("four")){
                    volumeToIncrease = 4;
                } else if (matcher.group(5).equals("five")){
                    volumeToIncrease = 5;
                } else {
                    volumeToIncrease = Integer.parseInt(matcher.group(5));
                }
                newVolume=volumeToIncrease+currentVolume;
                if (newVolume>maxVolume){
                    newVolume=maxVolume;
                }
                audioManager.setStreamVolume(streamType, newVolume, AudioManager.FLAG_SHOW_UI);
                textToSpeech.speak("Volume increased", TextToSpeech.QUEUE_ADD, null, "volume increased");
                Log.d("TAG", "Current volume at: " + newVolume);

            } //VOLUME FUNCTIONALITY FOR DECREASE VOLUME
            else if ((matcher.group(1).equals("decrease"))  || (matcher.group(1).equals("lower")) || (matcher.group(1).equals("turn down")) || (matcher.group(1).equals("decrement"))) {

                if (matcher.group(5).equals("one")) {
                    volumeToDecrease = 1;
                } else if (matcher.group(5).equals("two")) {
                    volumeToDecrease = 2;
                } else if (matcher.group(5).equals("three")) {
                    volumeToDecrease = 3;
                } else if (matcher.group(5).equals("four")) {
                    volumeToDecrease = 4;
                } else if (matcher.group(5).equals("five")) {
                    volumeToDecrease = 5;
                } else {
                    volumeToDecrease = Integer.parseInt(matcher.group(5));
                }
                newVolume = currentVolume - volumeToDecrease;


                if (newVolume < 1) {
                    newVolume = 1;
                }

                audioManager.setStreamVolume(streamType, newVolume, AudioManager.FLAG_SHOW_UI);
                textToSpeech.speak("Volume decreased", TextToSpeech.QUEUE_ADD, null, "volume decreased");
                Log.d("TAG", "Current volume at: " + newVolume);

            }

        }



    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


    public void testingFunction(String word) {
        Log.d("TAG", "I AM IN THE TESTING FUNCTION");
        if (isTranslationComplete) {
            int maxVolume = audioManager.getStreamMaxVolume(streamType);
            int currentVolume = audioManager.getStreamVolume(streamType);
            int volumeToIncrease = 0;
            int volumeToDecrease = 0;
            int newVolume = 0;

            Matcher matcher = pattern.matcher(word.toLowerCase());
            if (matcher.find()) {
                Log.d("TAG", "FOUND!");

                if (matcher.group(1).equals("increase") || (matcher.group(1).equals("increment")) || (matcher.group(1).equals("raise")) || (matcher.group(1).equals("turn up"))) {
                    if (matcher.group(5).equals("one")) {
                        volumeToIncrease = 1;
                    } else if (matcher.group(5).equals("two")) {
                        volumeToIncrease = 2;
                    } else if (matcher.group(5).equals("three")) {
                        volumeToIncrease = 3;
                    } else if (matcher.group(5).equals("four")) {
                        volumeToIncrease = 4;
                    } else if (matcher.group(5).equals("five")) {
                        volumeToIncrease = 5;
                    } else if (matcher.group(5).equals("six")) {
                        volumeToIncrease = 6;
                    } else if (matcher.group(5).equals("seven")) {
                        volumeToIncrease = 7;
                    } else if (matcher.group(5).equals("eight")) {
                        volumeToIncrease = 8;
                    } else if (matcher.group(5).equals("nine")) {
                        volumeToIncrease = 9;
                    }
                    else {
                        volumeToIncrease = Integer.parseInt(matcher.group(5));
                    }
                    newVolume = volumeToIncrease + currentVolume;
                    if (newVolume > maxVolume) {
                        newVolume = maxVolume;
                    }
                    audioManager.setStreamVolume(streamType, newVolume, AudioManager.FLAG_SHOW_UI);
                    textToSpeech.speak("Volume increased", TextToSpeech.QUEUE_ADD, null, "volume increased");
                    Log.d("TAG", "Current volume at: " + newVolume);

                } //VOLUME FUNCTIONALITY FOR DECREASE VOLUME
                else if ((matcher.group(1).equals("decrease")) || (matcher.group(1).equals("lower")) || (matcher.group(1).equals("turn down")) || (matcher.group(1).equals("decrement"))) {

                    if (matcher.group(5).equals("one")) {
                        volumeToDecrease = 1;
                    } else if (matcher.group(5).equals("two")) {
                        volumeToDecrease = 2;
                    } else if (matcher.group(5).equals("three")) {
                        volumeToDecrease = 3;
                    } else if (matcher.group(5).equals("four")) {
                        volumeToDecrease = 4;
                    } else if (matcher.group(5).equals("five")) {
                        volumeToDecrease = 5;
                    } else {
                        volumeToDecrease = Integer.parseInt(matcher.group(5));
                    }
                    newVolume = currentVolume - volumeToDecrease;


                    if (newVolume < 1) {
                        newVolume = 1;
                    }

                    audioManager.setStreamVolume(streamType, newVolume, AudioManager.FLAG_SHOW_UI);
                    textToSpeech.speak("Volume decreased", TextToSpeech.QUEUE_ADD, null, "volume decreased");
                    Log.d("TAG", "Current volume at: " + newVolume);

                }

            }
        }
    }
}
