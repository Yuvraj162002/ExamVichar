package com.example.android.studentsapp.StateActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.PrevStatesDesc;
import com.example.android.studentsapp.databinding.ActivityStatePrevYearPaperBinding;
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

public class StatePrevYearPaper extends AppCompatActivity {

    ActivityStatePrevYearPaperBinding b;
    String finalExamName;
    String finalYear;
    String finalStateName;
    MyLoaders myLoaders;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityStatePrevYearPaperBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        myLoaders = new MyLoaders();

        initializingValues();

        elseIfTrail();
    }

    private void elseIfTrail() {
        if(finalExamName.equals("PUBLIC SERVICE COMMISSION EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Andhra Pradesh") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Bihar") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Delhi") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Gujarat") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Haryana") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Jammu & Kashmir") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Jharkhand") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Karnataka") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Kerala") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Madhya Pradesh") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Maharashtra") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Meghalaya") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Nagaland") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("Odisha") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Telangana") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePre();
        }
        else if(finalStateName.equals("Uttarakhand") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else if(finalStateName.equals("West Bengal") && finalExamName.equals("POLICE DEPARTMENT EXAM")){
            setUpFirebasePolice();
        }
        else {
            setUpFirebase();
        }
    }

    private void setUpFirebasePolice() {
        myLoaders.settingUpLoader(StatePrevYearPaper.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalStateName).child(finalExamName).child("subInspector").child("prevPaper").child(finalYear).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        PrevStatesDesc value = snapshot.getValue(PrevStatesDesc.class);

                        new RetrievePrevStates().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void setUpFirebasePre() {
        myLoaders.settingUpLoader(StatePrevYearPaper.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalStateName).child(finalExamName).child("prelims").child("prevPaper").child(finalYear).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        PrevStatesDesc value = snapshot.getValue(PrevStatesDesc.class);

                        new RetrievePrevStates().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void initializingValues() {
        finalStateName = getIntent().getStringExtra("stateName");
        finalExamName = getIntent().getStringExtra("examName");
        finalYear = getIntent().getStringExtra("year");
    }

    private void setUpFirebase() {
        myLoaders.settingUpLoader(StatePrevYearPaper.this);
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalStateName).child(finalExamName).child("prevPaper").child(finalYear).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        PrevStatesDesc value = snapshot.getValue(PrevStatesDesc.class);

                        new RetrievePrevStates().execute(value.url);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private class RetrievePrevStates  extends AsyncTask<String,Void, InputStream> {
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

    @Override
    protected void onDestroy() {
        myLoaders.hideLoadingDialog();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StateExams.class);
        intent.putExtra("stateName",finalStateName);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,StateExams.class);
                intent.putExtra("stateName",finalStateName);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

