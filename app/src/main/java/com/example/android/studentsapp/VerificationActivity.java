package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivityVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding b;
    private String OTP;
    private FirebaseAuth firebaseAuth;
    SmsVerifyCatcher smsVerifyCatcher;
    String code1;
    String code2;
    String code3;
    String code4;
    String code5;
    String code6;
    private Dialog dialog;
    private EditText[] otpEt = new EditText[6];
    MyLoaders myLoaders;
    PhoneAuthentication phoneAuthentication;
    String mobile;
    private FirebaseAuth mAuth;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        mAuth = FirebaseAuth.getInstance();
        phoneAuthentication = new PhoneAuthentication();
        firebaseAuth = FirebaseAuth.getInstance();
        myLoaders = new MyLoaders();


        otpInitialization();


        OTP = getIntent().getStringExtra("verificationId");
        mobile = getIntent().getStringExtra("countryPhone");
        //new MySMSBroadcastReceiver().setEditText(b.otpEditBox1);
        otpAutofill();


        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        b.textView10.setVisibility(View.VISIBLE);
                        b.textView9.setVisibility(View.VISIBLE);
                        b.textView14.setVisibility(View.VISIBLE);
                        b.textView16.setVisibility(View.VISIBLE);
                        new CountDownTimer(18000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                b.textView16.setText("seconds remaining: " + millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                b.textView10.setVisibility(View.GONE);
                                b.textView9.setVisibility(View.GONE);
                                b.textView17.setVisibility(View.VISIBLE);
                                b.textView16.setText("Over!");
                            }

                        }.start();
                    }
                });
            }
        }, 60000); // Set your time period here //


        b.textView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerificationActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                mAuth = FirebaseAuth.getInstance();
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential)
                    {

                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e)
                    {
                        Toast.makeText(VerificationActivity.this, "Server issues !", Toast.LENGTH_SHORT).show();
                    }



                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token)
                    {


                        Toast.makeText(VerificationActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                        OTP = verificationId;

                        //mResendToken = token;

                        // ...
                    }
                };

                //String phoneNumber=Settings.PREFIX + Settings.PREFIX_PHONE;

                String phoneNumber="+91" + mobile;

                Log.e("number","credential=-=-=>>>22222>>"+phoneNumber);

                if(phoneNumber!=null && !phoneNumber.isEmpty())
                {
                    startPhoneNumberVerification(phoneNumber);
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                myLoaders.hideLoadingDialog();
            }
        }, 2000);


    }

    private void startPhoneNumberVerification(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(VerificationActivity.this, "done", Toast.LENGTH_SHORT).show();

                        } else
                        {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(VerificationActivity.this, "chutiye", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void otpInitialization() {
        otpEt[0] = (EditText) findViewById(R.id.otp_edit_box1);
        otpEt[1] = (EditText) findViewById(R.id.otp_edit_box2);
        otpEt[2] = (EditText) findViewById(R.id.otp_edit_box3);
        otpEt[3] = (EditText) findViewById(R.id.otp_edit_box4);
        otpEt[4] = (EditText) findViewById(R.id.otp_edit_box5);
        otpEt[5] = (EditText) findViewById(R.id.otp_edit_box6);

        setOtpEditTextHandler();

    }

    private void setOtpEditTextHandler() {
        for (int i = 0;i < 6;i++) { //Its designed for 6 digit OTP
            final int iVal = i;

            otpEt[iVal].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(iVal == 5 && !otpEt[iVal].getText().toString().isEmpty()) {
                        otpEt[iVal].clearFocus(); //Clears focus when you have entered the last digit of the OTP.
                    } else if (!otpEt[iVal].getText().toString().isEmpty()) {
                        otpEt[iVal+1].requestFocus(); //focuses on the next edittext after a digit is entered.
                    }
                }
            });

            otpEt[iVal].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        return false; //Dont get confused by this, it is because onKeyListener is called twice and this condition is to avoid it.
                    }
                    if(keyCode == KeyEvent.KEYCODE_DEL &&
                            otpEt[iVal].getText().toString().isEmpty() && iVal != 0) {
//this condition is to handel the delete input by users.
                        otpEt[iVal-1].setText("");//Deletes the digit of OTP
                        otpEt[iVal-1].requestFocus();//and sets the focus on previous digit
                    }
                    return false;
                }
            });
        }
    }


    public void moveToHome(View view) {
        String c1 = b.otpEditBox1.getText().toString().trim();
        String c2 = b.otpEditBox2.getText().toString().trim();
        String c3 = b.otpEditBox3.getText().toString().trim();
        String c4 = b.otpEditBox4.getText().toString().trim();
        String c5 = b.otpEditBox5.getText().toString().trim();
        String c6 = b.otpEditBox6.getText().toString().trim();

        String otp = c1+c2+c3+c4+c5+c6;

        if(!otp.isEmpty()){
            myLoaders.settingUpLoader(VerificationActivity.this);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, otp);
            signIn(credential);
        }
        else{
            Toast.makeText(this, "Enter correct OTP!", Toast.LENGTH_SHORT).show();
        }
    }

    private void otpAutofill() {

        myLoaders.settingUpLoader(VerificationActivity.this);

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                code1 = message.substring(0,1);
                code2 = message.substring(1,2);
                code3 = message.substring(2,3);
                code4 = message.substring(3,4);
                code5 = message.substring(4,5);
                code6 = message.substring(5,6);//Parse verification code
                b.otpEditBox1.setText(""+code1);
                b.otpEditBox2.setText(""+code2);
                b.otpEditBox3.setText(""+code3);
                b.otpEditBox4.setText(""+code4);
                b.otpEditBox5.setText(""+code5);
                b.otpEditBox6.setText(""+code6);//set code in edit text
                //then you can send verification code to server
                String code = code1+code2+code3+code4+code5+code6;
                if(code.length() == 6){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, code);
                    signIn(credential);
                }
                else{
                    myLoaders.hideLoadingDialog();
                    Toast.makeText(VerificationActivity.this, "Enter Correct OTP !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            myLoaders.hideLoadingDialog();
                        }
                    }, 2000);
                    Intent in = new Intent(VerificationActivity.this,HomeActivity.class);
                    startActivity(in);
                    finish();
                }
                else{
                    Toast.makeText(VerificationActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onDestroy() {
        myLoaders.hideLoadingDialog();
        super.onDestroy();
    }


    /**
     * need for Android 6 real time permissions
     */
    private void resendVerificationCode(){

    }



}
