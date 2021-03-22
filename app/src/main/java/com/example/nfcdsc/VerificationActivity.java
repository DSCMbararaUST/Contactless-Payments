package com.example.nfcdsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */

public class VerificationActivity extends AppCompatActivity {

    //UI Views
    private EditText otp_editText;
    private Button verify_btn;

    //Global variable storing the OTP code from intent.
    String OTPcode;

    //Firebase variables
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //Binding the views to java logic
        otp_editText = findViewById(R.id.otp_code);
        verify_btn = findViewById(R.id.verify_btn);

        OTPcode = getIntent().getStringExtra("OTP_CODE");

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Capturing the OTP CODE entered by the user.
                String verification_code = otp_editText.getText().toString();

                if (!verification_code.isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTPcode,
                            verification_code);

                    signIn(credential);
                }else{
                    ToastMaker.toast(VerificationActivity.this,
                            "PLEASE ENTER THE OTP CODE THAT HAS BEEN SENT 🤨");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Checking if user is logged in already
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if (currentUser!=null){
            routeToMain();
        }else{
            ToastMaker.toast(VerificationActivity.this, "You are not signed up yet");
        }
    }

    //Function to route a  user to the main activity for payment if their are logged in
    private void routeToMain(){
        startActivity(new Intent(VerificationActivity.this, MainActivity.class));
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