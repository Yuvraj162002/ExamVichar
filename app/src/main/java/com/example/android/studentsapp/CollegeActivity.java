package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.studentsapp.StateActivities.StatesActivity;
import com.example.android.studentsapp.databinding.ActivityCollegeBinding;
import com.example.android.studentsapp.databinding.ActivityNotesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollegeActivity extends AppCompatActivity {

    ActivityCollegeBinding b;
    String courses[];
    ArrayList<String> options = new ArrayList<>();
    String getBranch;
    String getCourse;
    private DatabaseReference myBranchRef;
    boolean isThirdGuest;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCollegeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        checkingConnectivity();
        descisionMaking();

        initialisingActivity();


    }

    private void initialisingActivity() {
        isThirdGuest = getIntent().getBooleanExtra("isGuest", false);

        myBranchRef = FirebaseDatabase.getInstance().getReference("PDF");

        courses = new String[]{"BTECH", "BCOM", "BSC"};

        arrayAdapterForCourse();


        SettingUpItemClickListener();
    }

    private void descisionMaking() {
        if(connected){
            initialisingActivity();
        }
        else{
            Intent intent = new Intent(CollegeActivity.this, NetWorkActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CollegeActivity.this.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }

    private void SettingUpItemClickListener() {
        b.autoCompleteCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                options.removeAll(options);
                getCourse = (String)parent.getItemAtPosition(position);
                b.autoCompleteCourse.setText(getCourse,false);
                myBranchRef.child(getCourse).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String value = dataSnapshot.getKey().toString();
                            options.add(value);

                        }
                        arrayAdapterForBranch();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });



        b.autoCompleteType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getBranch = (String)parent.getItemAtPosition(position);
                b.autoCompleteType.setText(getBranch,false);
            }
        });
    }


    private void arrayAdapterForBranch() {
        ArrayAdapter typeAdapter = new ArrayAdapter(this,R.layout.drop_down_view,options);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteType.setAdapter(typeAdapter);
    }

    private void arrayAdapterForCourse() {

        ArrayAdapter courseAdapter = new ArrayAdapter(this,R.layout.drop_down_view,courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteCourse.setAdapter(courseAdapter);
    }

    public void moveToCollegeBranches(View view) {
        if(!options.contains(b.autoCompleteType.getEditableText().toString().trim())){
            Toast.makeText(this, "Invalid topic name!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(CollegeActivity.this,BranchActivity.class);
        intent.putExtra("courseName",getCourse);
        intent.putExtra("branchName",getBranch);
        intent.putExtra("isThirdGuest", isThirdGuest);
        startActivity(intent);
    }
}