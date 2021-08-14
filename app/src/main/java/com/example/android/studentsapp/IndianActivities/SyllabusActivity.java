package com.example.android.studentsapp.IndianActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.ExamContent;
import com.example.android.studentsapp.Model.ExamDates;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivitySyllabusBinding;
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
import java.util.ArrayList;
import java.util.List;

public class SyllabusActivity extends AppCompatActivity {

    String examDetail;
    private DatabaseReference myRef;
    ActivitySyllabusBinding bind;
    ExamDates examDates;
    List<ExamContent> examContents;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySyllabusBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        myLoaders = new MyLoaders();


         examContents = new ArrayList<>();
         examDetail = getIntent().getStringExtra("examName");
         setUpFirebase();
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(SyllabusActivity.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");
        myRef.child(examDetail).child("syllabus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ExamContent value = snapshot.getValue(ExamContent.class);

                new RetrievePdfStream().execute(value.url);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

        class  RetrievePdfStream extends AsyncTask<String,Void, InputStream>{

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
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, IndianActivity.class));
        finish();
    }

    }

