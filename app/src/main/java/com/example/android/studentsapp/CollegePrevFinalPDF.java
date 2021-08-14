package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.CollegeDescription;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivityCollegePdfBinding;
import com.example.android.studentsapp.databinding.ActivityCollegePrevFinalPdfBinding;
import com.example.android.studentsapp.databinding.ActivityCollegePrevPdfBinding;
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

public class CollegePrevFinalPDF extends AppCompatActivity {

    ActivityCollegePrevFinalPdfBinding bind;
    DatabaseReference myRef;
    String finalCourseName;
    String finalType;
    String finalBranch;
    String finalSemesterName;
    String finalYear;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_prev_final_pdf);

        bind = ActivityCollegePrevFinalPdfBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        myLoaders = new MyLoaders();

        finalCourseName = getIntent().getStringExtra("courseName");
        finalType = getIntent().getStringExtra("typeName");
        finalBranch = getIntent().getStringExtra("branchName");
        finalSemesterName = getIntent().getStringExtra("semesterName");
        finalYear = getIntent().getStringExtra("yearName");

        setUpFirebase();
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(CollegePrevFinalPDF.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalCourseName).child(finalBranch).child(finalType).child(finalSemesterName).child(finalYear).
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