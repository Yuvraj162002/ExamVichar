package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivityRegistrationBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

public class RegistrationActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    ActivityRegistrationBinding b;
    boolean isGuest;
    MyLoaders myLoaders;
    int stateMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());


        essentialInfo();

        googleInfo();
        settingUpOnClick();


    }

    private void essentialInfo() {
        mAuth = FirebaseAuth.getInstance();
        myLoaders = new MyLoaders();
    }

    private void settingUpOnClick() {
        b.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void googleInfo() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void signIn() {
        googleSignInClient.signOut();
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            myLoaders.settingUpLoader(RegistrationActivity.this);
            FirebaseGoogleAuth(acc);
        }
        catch(ApiException e){
            Toast.makeText(this, "Couldn't Sign in!", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                myLoaders.hideLoadingDialog();
                Intent activityIntent = new Intent(RegistrationActivity.this,HomeActivity.class);
                startActivity(activityIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Sign in Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void moveToPhoneAuthentication(View view) {
        Intent intent = new Intent(RegistrationActivity.this,PhoneAuthentication.class);
        startActivity(intent);
    }

    public void moveToHome(View view) {
        isGuest = true;
        Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
        intent.putExtra("Guest",isGuest);
        startActivity(intent);
    }
}