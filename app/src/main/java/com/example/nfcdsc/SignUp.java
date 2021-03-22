package com.example.nfcdsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class SignUp extends AppCompatActivity {

    //Global variables for UI components
    private Button signup;
    private EditText fname, lname, phoneNo;

    //Global variables
    private String firstname, lastname, phone;

    //Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Instantiating firebase authentication
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Binding the xml to java
        signup = findViewById(R.id.sign_up_btn);
        fname = findViewById(R.id.first_name);
        lname = findViewById(R.id.second_name);
        phoneNo = findViewById(R.id.phone_number);

        signup.setOnClickListener(v -> {

            //Function Sending the OTP code request
            getOTP();
//            firstname = fname.getText().toString();
//            lastname = lname.getText().toString();
//            phone = phoneNo.getText().toString();
//
//            if (!firstname.isEmpty() && !lastname.isEmpty() && !phone.isEmpty()) {
//                Intent intent = new Intent(this, SettingsActivity.class);
//                intent.putExtra("USERNAME", firstname);
//                intent.putExtra("BALANCE", lastname);
//                intent.putExtra("CONTACT", phone);
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(this, " ENTER YOUR DETAILS ",
//                        Toast.LENGTH_SHORT).show();
//                fname.requestFocus();
//            }


        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                ToastMaker.toast(SignUp.this,"TRY AGAIN. VERIFICATION HAS NOT BEEN SENT ! ");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                //Delaying the routing to the Verification Activity
                // for user to manually enter the OTP code.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastMaker.toast(SignUp.this,"OTP CODE HAS BEEN SENT");
                        Intent otpIntent = new Intent(SignUp.this, VerificationActivity.class);
                        otpIntent.putExtra("OTP_CODE", s);
                        startActivity(otpIntent);
                    }
                }, 10000);


            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Checking if user is logged in already
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if (currentUser!=null){
            routeToMain();
        }else{
            ToastMaker.toast(SignUp.this,"You are not signed up yet");
            signup.requestFocus();
        }
    }

    private void getOTP(){

        //Capturing the users phone number
        phone = phoneNo.getText().toString();
        if (!phone.isEmpty()){
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(SignUp.this)
                    .setCallbacks(mCallBacks)
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    //Function to route a  user to the main activity for payment if their are logged in
    private void routeToMain(){
        startActivity(new Intent(SignUp.this, MainActivity.class));
        finish();
    }

    //Sign in function
    private void signIn(PhoneAuthCredential credential){
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //routing user to main activity if verification is done automatically
                    routeToMain();
                }
            }
        });
    }
}