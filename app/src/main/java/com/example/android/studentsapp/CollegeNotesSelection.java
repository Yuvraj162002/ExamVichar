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

import com.example.android.studentsapp.Model.CollegeDescription;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.databinding.ActivityCollegeNotesSelectionBinding;
import com.example.android.studentsapp.databinding.ActivityCollegePdfBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollegeNotesSelection extends AppCompatActivity {

    ActivityCollegeNotesSelectionBinding bind;
    ArrayList<String> subjects = new ArrayList<>();
    DatabaseReference myRef;
    String finalCourseName;
    String finalType;
    String finalBranch;
    String finalSemesterName;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind =  ActivityCollegeNotesSelectionBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        finalCourseName = getIntent().getStringExtra("courseName");
        finalType = getIntent().getStringExtra("typeName");
        finalBranch = getIntent().getStringExtra("branchName");
        finalSemesterName = getIntent().getStringExtra("semesterName");

        setUpFirebase();

        subjectDropdown();
    }

    private void subjectDropdown() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.drop_down_view,subjects);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.autoCompleteSubject.setAdapter(arrayAdapter);
    }

    private void setUpFirebase() {

        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        myRef.child(finalCourseName).child(finalBranch).child(finalType).child(finalSemesterName).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String value = dataSnapshot.getKey().toString();
                            subjects.add(value);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    public void moveToCollegePDF(View view) {
        if(bind.autoCompleteSubject.getText().toString().trim().equals("Subject")){
            Toast.makeText(this, "Please choose Subject!", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            checkingConnectivity();
            if(connected) {
                Intent intent = new Intent(CollegeNotesSelection.this, CollegePDF.class);
                intent.putExtra("courseName", finalCourseName);
                intent.putExtra("branchName", finalBranch);
                intent.putExtra("typeName", finalType);
                intent.putExtra("semesterName", finalSemesterName);
                intent.putExtra("subjectName", bind.autoCompleteSubject.getEditableText().toString().trim());
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CollegeNotesSelection.this.CONNECTIVITY_SERVICE);
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