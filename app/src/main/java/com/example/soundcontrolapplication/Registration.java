package com.example.soundcontrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobileno, editTextRegisterPassword, editTextRegisterConfirmPassword;
    ProgressBar progressBar;
    RadioGroup radioGroupGender;
    RadioButton radioButtonGenderSelection;
    private DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle("Register");

        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobileno = findViewById(R.id.editText_register_mobile);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);

        //RADIO BUTTON FOR GENDER SELECTION
        radioGroupGender = findViewById(R.id.radio_group_register_gender);
        radioGroupGender.clearCheck();

        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date picker dialog
                picker = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month) + "/" + year);
                    }
                }, year, month, day);
                picker.show();

            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderID = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGenderSelection = findViewById(selectedGenderID);


                String fullName = editTextRegisterFullName.getText().toString();
                String email = editTextRegisterEmail.getText().toString();
                String dateOfBirth = editTextRegisterDoB.getText().toString();
                String mobileNumber = editTextRegisterMobileno.getText().toString();
                String password = editTextRegisterPassword.getText().toString();
                String confirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String textGender;

                String mobileRegex = "[0][7][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(mobileNumber);

                if (TextUtils.isEmpty(fullName)){
                    Toast.makeText(getApplicationContext(), "Please enter your fullname", Toast.LENGTH_LONG).toString();
                    editTextRegisterFullName.setError("Fullname is required");
                    editTextRegisterFullName.requestFocus();

                } else if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_LONG).toString();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_LONG).toString();
                    editTextRegisterEmail.setError("Please enter a valid email");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(dateOfBirth)){
                    Toast.makeText(getApplicationContext(), "Please enter your date of birth", Toast.LENGTH_LONG).toString();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();

                } else if (radioGroupGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(), "Select one of the options", Toast.LENGTH_LONG).toString();
                    radioButtonGenderSelection.setError("Gender is required");
                    radioButtonGenderSelection.requestFocus();


                } else if (TextUtils.isEmpty(mobileNumber)){
                    Toast.makeText(getApplicationContext(), "Please enter a mobile number", Toast.LENGTH_LONG).toString();
                    editTextRegisterMobileno.setError("Mobile number is required");
                    editTextRegisterMobileno.requestFocus();
                } else if (mobileNumber.length()!=11){
                    Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_LONG).toString();
                    editTextRegisterMobileno.setError("Mobile number should be 11 digits");
                    editTextRegisterMobileno.requestFocus();
                } else if (!mobileMatcher.find()){
                    Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_LONG).toString();
                    editTextRegisterMobileno.setError("Mobile number is not valid");
                    editTextRegisterMobileno.requestFocus();
                }

                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).toString();
                    editTextRegisterPassword.setError("Password is required");
                    editTextRegisterPassword.requestFocus();
                } else if (password.length()<6){
                    Toast.makeText(getApplicationContext(), "Please enter a password longer than 6 characters", Toast.LENGTH_LONG).toString();
                    editTextRegisterPassword.setError("Password is Weak ");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).toString();
                    editTextRegisterConfirmPassword.setError("Password is required");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (!password.equals(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).toString();
                    editTextRegisterConfirmPassword.setError("Confirm Password should match password");
                    editTextRegisterConfirmPassword.requestFocus();
                    editTextRegisterConfirmPassword.clearComposingText();
                    editTextRegisterPassword.clearComposingText();
                } else {
                    textGender = radioButtonGenderSelection.getText().toString();
                    registerUser(fullName, email, mobileNumber, dateOfBirth, textGender, password);

                }


            }
        });


    }

    public void registerUser(String fn, String textEmail, String mb, String dof, String gender, String psw){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, psw).addOnCompleteListener(Registration.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "User registration successful!", Toast.LENGTH_LONG).show();
                            //AUNTHENTICATE USER
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Enter user data into the firebase
                            WriteUserDetails writeUserDetails = new WriteUserDetails(fn, dof, mb, gender);

                            //Extracting user reference from the database from "Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(Registration.this, bluetoothScreen.class);
                                        //PREVENTS USER FROM RETURNING BACK TO PREVIOUS ACITIVTY
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        //CLOSES REGISTRATION ACTIVITY
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Registration not successful. Please try again!", Toast.LENGTH_LONG).show();

                                    }


                                }
                            });

                        } else {
                            try{
                                throw task.getException();

                            } catch (FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterEmail.setError("Your email is invalid");
                                editTextRegisterEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                editTextRegisterEmail.setError("Email is already registed");
                                editTextRegisterEmail.requestFocus();
                            }catch (Exception e){
                                Log.d("TAG", e.getMessage());
                            }
                        }
                    }
                });
    }
}