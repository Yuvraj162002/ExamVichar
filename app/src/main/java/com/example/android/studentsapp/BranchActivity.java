package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.studentsapp.databinding.ActivityBranchBinding;
import com.example.android.studentsapp.databinding.ActivityCollegeBinding;
import com.example.android.studentsapp.databinding.ChooseGuestDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BranchActivity extends AppCompatActivity {

    ActivityBranchBinding b;
    private DatabaseReference myBranchRef;
    ArrayList<String> options = new ArrayList<>();
    ArrayList<String> topic = new ArrayList<>();
    ArrayList<String> topic2 = new ArrayList<>();
    String courseDetail;
    String branchDetail;
    String getType;
    boolean isGuest;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityBranchBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        isGuest = getIntent().getBooleanExtra("isThirdGuest", false);


        essentialInfo();

        getDataFromFirebase();

        adapterForType();

        gettingTopicsFromFirebase();

        adapterForTopics();
    }

    private void adapterForTopics() {
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this,R.layout.drop_down_view,topic);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteSemester.setAdapter(arrayAdapter2);
    }

    private void gettingTopicsFromFirebase() {

        b.autoCompleteType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                topic.removeAll(topic);
                getType = (String)parent.getItemAtPosition(position);
                b.autoCompleteType.setText(getType,false);

                if(getType.equals("SYLLABUS")){

                    b.proceedToPDF.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkingConnectivity();
                            if(connected) {
                                Intent intent = new Intent(BranchActivity.this, CollegeSyllabusPDF.class);
                                intent.putExtra("courseName", courseDetail);
                                intent.putExtra("branchName", branchDetail);
                                intent.putExtra("typeName", getType);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(BranchActivity.this, "NO CONNECTION !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                else if(!getType.equals("SYLLABUS") && isGuest == true){
                    ChooseGuestDialogBinding binding = ChooseGuestDialogBinding.inflate(LayoutInflater.from(BranchActivity.this));

                    new MaterialAlertDialogBuilder(BranchActivity.this,R.style.CustomDialogTheme2)
                            .setView(binding.getRoot())
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }

                else{
                    b.textInputLayout2.setVisibility(View.VISIBLE);

                    myBranchRef.child(courseDetail).child(branchDetail).child(getType)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String value2 = dataSnapshot.getKey().toString();
                                        topic.add(value2);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                }

            }
        });
    }

    private void adapterForType() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.drop_down_view,options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteType.setAdapter(arrayAdapter);
    }

    private void getDataFromFirebase() {
        Toast.makeText(this,branchDetail, Toast.LENGTH_SHORT).show();
        myBranchRef.child(courseDetail).child(branchDetail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String value = dataSnapshot.getKey().toString();
                    options.add(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void essentialInfo() {
        courseDetail = getIntent().getStringExtra("courseName");
        branchDetail = getIntent().getStringExtra("branchName");
        myBranchRef = FirebaseDatabase.getInstance().getReference("PDF");
    }

    public void moveToCollegePDF(View view) {
        if(b.autoCompleteType.getText().toString().trim().equals("Type")){
            Toast.makeText(this, "Please choose Type!", Toast.LENGTH_LONG).show();
            return;
        }

        if(b.autoCompleteSemester.getText().toString().trim().equals("Semester")){
            Toast.makeText(this, "Please choose Semester!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!topic.contains(b.autoCompleteSemester.getEditableText().toString().trim())){
            Toast.makeText(this, "Invalid topic name!", Toast.LENGTH_LONG).show();
            return;
        }
        

        if (b.autoCompleteType.getText().toString().trim().equals("Previous Year Papers")) {
            Intent intent = new Intent(BranchActivity.this, CollegePrevPDF.class);
            intent.putExtra("courseName", courseDetail);
            intent.putExtra("branchName", branchDetail);
            intent.putExtra("typeName", getType);
            intent.putExtra("semesterName", b.autoCompleteSemester.getEditableText().toString().trim());
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(BranchActivity.this, CollegeNotesSelection.class);
            intent.putExtra("courseName", courseDetail);
            intent.putExtra("branchName", branchDetail);
            intent.putExtra("typeName", getType);
            intent.putExtra("semesterName", b.autoCompleteSemester.getEditableText().toString().trim());
            startActivity(intent);
        }
    }

    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(BranchActivity.this.CONNECTIVITY_SERVICE);
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