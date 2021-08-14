package com.example.android.studentsapp.StateActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.StateSyllabus;
import com.example.android.studentsapp.databinding.ActivityStatesSyllabusBinding;
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

public class StatesSyllabusActivity extends AppCompatActivity {

    String stateDetail;
    String examDetail;
    private DatabaseReference myRef;
    ActivityStatesSyllabusBinding b;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityStatesSyllabusBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        myLoaders = new MyLoaders();

        stateDetail = getIntent().getStringExtra("stateName");
        examDetail = getIntent().getStringExtra("examName");

        setUpFirebase();
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(StatesSyllabusActivity.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");
        myRef.child(stateDetail).child(examDetail).child("syllabus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                StateSyllabus value = snapshot.getValue(StateSyllabus.class);

                new RetrieveStatesSyllabusPDF().execute(value.url);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    class  RetrieveStatesSyllabusPDF extends AsyncTask<String,Void, InputStream> {

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
            b.pdfView.fromStream(inputStream).load();

        }
    }
}