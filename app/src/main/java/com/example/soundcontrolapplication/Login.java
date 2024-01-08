package com.example.soundcontrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import kotlin.UInt;

public class Login extends AppCompatActivity {


    private EditText editTextEmail, editTextPassword;
    FirebaseAuth userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        editTextEmail = findViewById(R.id.edit_text_login);
        editTextPassword = findViewById(R.id.edit_text_password);

        userProfile = FirebaseAuth.getInstance();


        //login button

        Button myButtonLogin = findViewById(R.id.button_login);
        myButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if ((TextUtils.isEmpty(textEmail))){
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Email is required!");
                    editTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(getApplicationContext(), "Please re-enter the email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Enter a valid email address!");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Password is required!");
                    editTextPassword.requestFocus();
                } else {
                    loginUser(textEmail, password);
                }
            }
        });



    }

    private void loginUser(String email, String password){
        userProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "SUCCESSFULL");

                    FirebaseUser firebaseUser = userProfile.getCurrentUser();

                    //check if email is verified
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this, "You logged in successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent (Login.this, bluetoothScreen.class));
                        finish();

                    } else {
                        firebaseUser.sendEmailVerification();
                        userProfile.signOut();
                        showAlert();
                    }


                } else {
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e){
                        editTextEmail.setError("User does not exist or is no longer valid. Please register again!");
                        editTextEmail.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        editTextEmail.setError("Invalid credentials. Try again!");
                        editTextEmail.requestFocus();
                    } catch (Exception e){
                        Log.d("TAG", e.getMessage());
                    }
                    Toast.makeText(Login.this, "Something went wrong! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showAlert() {
        //Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email. You cannot proceed without email verification");

        /*
        //open email app if continue is pressed
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //email opened in a new window and not on our app
                startActivity(intent);
            }
        });

         */

        //create the dialog
        AlertDialog alertDialog = builder.create();

        //show dialog
        alertDialog.show();
    }

    //checks to see if user is logged in, If so, we send user to BluetoothActivity screen
    @Override
    protected void onStart(){
        super.onStart();
        if (userProfile.getCurrentUser()!=null){
            //user logged in

            startActivity(new Intent (Login.this, bluetoothScreen.class));
            finish();
        }


    }
}