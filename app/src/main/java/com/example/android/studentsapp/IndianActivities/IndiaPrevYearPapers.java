package com.example.android.studentsapp.IndianActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.PrevIndiaDesc;
import com.example.android.studentsapp.databinding.ActivityIndiaPrevYearPapersBinding;
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

public class IndiaPrevYearPapers extends AppCompatActivity {

    ActivityIndiaPrevYearPapersBinding b;
    String finalExamName;
    String finalYear;
    String subExamName;
    String subSubExamName;
    String subSubSubExamName;
    String subSubSubSubExamName;
    MyLoaders myLoaders;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityIndiaPrevYearPapersBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        myLoaders = new MyLoaders();

        initialisingValues();

        setUpFirebase();
    }

    private void initialisingValues() {
        finalExamName = getIntent().getStringExtra("examName");
        finalYear = getIntent().getStringExtra("year");

    }

    private void setUpFirebase() {

        myLoaders.settingUpLoader(IndiaPrevYearPapers.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        if(finalExamName.equals("JEE")){
            subExamName = getIntent().getStringExtra("subExamName");
            myRef.child(finalExamName).child(subExamName).child("prevPaper").child(finalYear).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                            new RetrievePrevPapersIndia().execute(value.url);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }
        else if(finalExamName.equals("SBI PO")){
            subExamName = getIntent().getStringExtra("subExamName");
            myRef.child(finalExamName).child(subExamName).child("prevPaper").child(finalYear).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                            new RetrievePrevPapersIndia().execute(value.url);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }

        else if( finalExamName.equals("UPSC")){
            subExamName = getIntent().getStringExtra("subExamName");
            if(subExamName.equals("prelims")) {
                myRef.child(finalExamName).child(subExamName).child("prevPaper").child(finalYear).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                                new RetrievePrevPapersIndia().execute(value.url);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
            else if(subExamName.equals("mains")){

                subSubExamName = getIntent().getStringExtra("subsubExamName");
                if(subSubExamName.equals("qualifyingPapers")) {
                    myRef.child(finalExamName).child(subExamName).child(subSubExamName).child("prevPaper").child(finalYear).
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                                    new RetrievePrevPapersIndia().execute(value.url);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                }
                else if(subSubExamName.equals("passMerit")){
                    subSubSubExamName = getIntent().getStringExtra("subsubsubExam");
                    if(subSubSubExamName.equals("optional")){
                        subSubSubSubExamName = getIntent().getStringExtra("subsubsubsubExam");
                        myRef.child(finalExamName).child(subExamName).child(subSubExamName).
                                child("optional").child("AGRICULTURE").child("prevPaper").child(finalYear).
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                                        new RetrievePrevPapersIndia().execute(value.url);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                    }
                    else {
                        myRef.child(finalExamName).child(subExamName).child(subSubExamName).
                                child(subSubSubExamName).child("prevPaper").child(finalYear).
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                                        new RetrievePrevPapersIndia().execute(value.url);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }
        }

        else{

        myRef.child(finalExamName).child("prevPaper").child(finalYear).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        PrevIndiaDesc value = snapshot.getValue(PrevIndiaDesc.class);

                        new RetrievePrevPapersIndia().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        }
    }

    private class RetrievePrevPapersIndia extends AsyncTask<String,Void, InputStream> {
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