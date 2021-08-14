package com.example.android.studentsapp.StateActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.StatesDesc;
import com.example.android.studentsapp.databinding.ActivityStateNotesPdfBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StateNotesPDF extends AppCompatActivity {

    ActivityStateNotesPdfBinding bind;
    private DatabaseReference myReference;
    String finalExamName;
    String finalSubject;
    String finalTopic;
    String finalState;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityStateNotesPdfBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        myLoaders = new MyLoaders();

        finalState = getIntent().getStringExtra("stateName");
        finalExamName = getIntent().getStringExtra("examName");
        finalSubject = getIntent().getStringExtra("subjectName");
        finalTopic = getIntent().getStringExtra("topicName");

        setUpFirebase();
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(StateNotesPDF.this);
        myReference = FirebaseDatabase.getInstance().getReference("PDF");

        myReference.child(finalState).child(finalExamName).child("notes").child(finalSubject).child(finalTopic).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        StatesDesc value = snapshot.getValue(StatesDesc.class);

                        new RetrieveStateNotesPdfStream().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private class RetrieveStateNotesPdfStream extends AsyncTask<String,Void, InputStream>{
        @Override
        protected InputStream doInBackground(String... strings) {

                InputStream inputStream = null;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if(urlConnection.getResponseCode() == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    }
                }catch (IOException e){
                    return null;
                }
                return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            myLoaders.hideLoadingDialog();
            bind.pdfViewStates.fromStream(inputStream).load();

        }
    }
}