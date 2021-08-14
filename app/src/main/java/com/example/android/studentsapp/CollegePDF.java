package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.CollegeDescription;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.NotesDesc;
import com.example.android.studentsapp.databinding.ActivityCollegePdfBinding;
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

public class CollegePDF extends AppCompatActivity {

    ActivityCollegePdfBinding bind;
    DatabaseReference myRef;
    String finalCourseName;
    String finalType;
    String finalBranch;
    String finalSemesterName;
    String finalSubject;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCollegePdfBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        myLoaders = new MyLoaders();

        essentialInfo();

        setUpFirebase();
    }

    private void essentialInfo() {
        finalCourseName = getIntent().getStringExtra("courseName");
        finalType = getIntent().getStringExtra("typeName");
        finalBranch = getIntent().getStringExtra("branchName");
        finalSemesterName = getIntent().getStringExtra("semesterName");
        finalSubject = getIntent().getStringExtra("subjectName");
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(CollegePDF.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalCourseName).child(finalBranch).child(finalType).child(finalSemesterName).child(finalSubject).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        CollegeDescription value = snapshot.getValue(CollegeDescription.class);

                        new RetrieveCollegePDFStrem().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private class RetrieveCollegePDFStrem extends AsyncTask<String,Void, InputStream> {
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