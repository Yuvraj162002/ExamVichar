package com.example.android.studentsapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivityPhoneAuthenticationBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final int RESOLVE_HINT = 12;
    ActivityPhoneAuthenticationBinding bind;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String onVerificationId;

    private FirebaseAuth mAuth;

    private static final String Tag = "MAIN_TAG";

    private Dialog dialog;

    MyLoaders myLoaders;
    String countryPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPhoneAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        mAuth = FirebaseAuth.getInstance();
        myLoaders = new MyLoaders();

        setUpHideError();
        requestHint();
        signInWithPhone();

    }

    private void setUpHideError() {
        bind.phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bind.phoneNumber.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void requestHint()  {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks( PhoneAuthentication.this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {
                        Toast.makeText(PhoneAuthentication.this, "connection failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String countryNo = credential.getId().replace("+91","");
                if (credential != null) {

                    bind.phoneNumber.setText(countryNo);

                    // credential.getId();  <-- will need to process phone number string
                }
            }
        }
    }
    private void signInWithPhone() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                myLoaders.hideLoadingDialog();
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(PhoneAuthentication.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(PhoneAuthentication.this, "Too Many Requests For OTP in a short period",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String verificationId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Toast.makeText(PhoneAuthentication.this, "Code sent", Toast.LENGTH_SHORT).show();

                myLoaders.hideLoadingDialog();
                Intent intent = new Intent(PhoneAuthentication.this,VerificationActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("countryPhone", bind.phoneNumber.getText().toString().trim());

                startActivity(intent);
            }
        };

        bind.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = bind.phoneNumber.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNo)){
                    bind.phoneNumber.setError("Enter Number");
                    return;
                }
                bind.phoneNumber.setError(null);
                startPhoneNumberVerification(phoneNo);
            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        myLoaders.settingUpLoader(PhoneAuthentication.this);

        countryPhone = "+91"+phone;

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(countryPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(PhoneAuthentication.this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential){
        myLoaders.settingUpLoader(PhoneAuthentication.this);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        myLoaders.hideLoadingDialog();
                        String phone = mAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneAuthentication.this, "Logged in as"+phone,
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PhoneAuthentication.this,VerificationActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                myLoaders.hideLoadingDialog();
                Toast.makeText(PhoneAuthentication.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}