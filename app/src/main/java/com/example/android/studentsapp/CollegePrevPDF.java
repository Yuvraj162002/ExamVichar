package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.studentsapp.databinding.ActivityCollegePrevPdfBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollegePrevPDF extends AppCompatActivity {

    ActivityCollegePrevPdfBinding bind;
    ArrayList<String> papers = new ArrayList<>();
    DatabaseReference myRefPrev;
    String final2CourseName;
    String final2Type;
    String final2Branch;
    String final2SemesterName;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind =  ActivityCollegePrevPdfBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        final2CourseName = getIntent().getStringExtra("courseName");
        final2Type = getIntent().getStringExtra("typeName");
        final2Branch = getIntent().getStringExtra("branchName");
        final2SemesterName = getIntent().getStringExtra("semesterName");

        setUpFirebase();

        paperDropdown();
    }

    private void setUpFirebase() {

        myRefPrev = FirebaseDatabase.getInstance().getReference("PDF");

        myRefPrev.child(final2CourseName).child(final2Branch).child(final2Type).child(final2SemesterName).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String value = dataSnapshot.getKey().toString();
                            papers.add(value);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void paperDropdown() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.drop_down_view,papers);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.autoCompleteYear.setAdapter(arrayAdapter);
    }

    public void moveToPrevCollegePDF(View view) {
        if(bind.autoCompleteYear.getText().toString().trim().equals("Subject")){
            Toast.makeText(this, "Please choose Subject!", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            checkingConnectivity();
            if(connected) {
                Intent intent = new Intent(CollegePrevPDF.this, CollegePrevFinalPDF.class);
                intent.putExtra("courseName", final2CourseName);
                intent.putExtra("branchName", final2Branch);
                intent.putExtra("typeName", final2Type);
                intent.putExtra("semesterName", final2SemesterName);
                intent.putExtra("yearName", bind.autoCompleteYear.getEditableText().toString().trim());
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CollegePrevPDF.this.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }
}