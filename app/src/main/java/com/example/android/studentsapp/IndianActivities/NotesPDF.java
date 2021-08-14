package com.example.android.studentsapp.IndianActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.NotesDesc;
import com.example.android.studentsapp.databinding.ActivityNotesPdfBinding;
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

public class NotesPDF extends AppCompatActivity {

    ActivityNotesPdfBinding bind;
    DatabaseReference myRef;
    String finalExamName;
    String finalSubSubjectName;
    String finalSubject;
    String finalTopic;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityNotesPdfBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        myLoaders = new MyLoaders();


        finalExamName = getIntent().getStringExtra("examName");

        if(finalExamName.equals("GATE")) {
            finalSubject = getIntent().getStringExtra("subjectName");
            setUpFirebaseForGate();
        }

        else if(finalExamName.equals("CAT")){
            finalSubject = getIntent().getStringExtra("subjectName");
            setUpFirebaseForGate();
        }

        else if(finalExamName.equals("CA")){
            finalSubject = getIntent().getStringExtra("subjectName");
            setUpFirebaseForGate();
        }

        else if(finalExamName.equals("CLAT")){
            finalSubject = getIntent().getStringExtra("subjectName");
            setUpFirebaseForGate();
        }
        else if(finalExamName.equals("SBI PO")){
            finalSubject = getIntent().getStringExtra("subjectName");
            finalTopic = getIntent().getStringExtra("topicName");
            finalSubSubjectName = getIntent().getStringExtra("subExamName");
            setUpFirebaseForSbi();
        }

        else{
            finalSubject = getIntent().getStringExtra("subjectName");
            finalTopic = getIntent().getStringExtra("topicName");

            setUpFirebase();
        }
    }

    private void setUpFirebaseForSbi() {

        myLoaders.settingUpLoader(NotesPDF.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalExamName).child(finalSubSubjectName).child("notes").child(finalSubject).child(finalTopic).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        NotesDesc value = snapshot.getValue(NotesDesc.class);

                        new RetrieveNotesPdfStream().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void setUpFirebaseForGate() {
        myLoaders.settingUpLoader(NotesPDF.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalExamName).child("notes").child(finalSubject).child("completeNotes").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        NotesDesc value = snapshot.getValue(NotesDesc.class);

                        new RetrieveNotesPdfStream().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(NotesPDF.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalExamName).child("notes").child(finalSubject).child(finalTopic).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                NotesDesc value = snapshot.getValue(NotesDesc.class);

                new RetrieveNotesPdfStream().execute(value.url);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private class  RetrieveNotesPdfStream extends AsyncTask<String,Void, InputStream> {

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
            bind.pdfView.fromStream(inputStream).load();
        }
    }
}