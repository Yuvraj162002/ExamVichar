package com.example.android.studentsapp.StateActivities;

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

import com.example.android.studentsapp.BranchActivity;
import com.example.android.studentsapp.ComingSoon;
import com.example.android.studentsapp.R;
import com.example.android.studentsapp.databinding.ActivityStateExamsBinding;
import com.example.android.studentsapp.databinding.ChooseExamDialogBinding;
import com.example.android.studentsapp.databinding.ChooseGuestDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StateExams extends AppCompatActivity {

    ActivityStateExamsBinding b;
    String stateDetail;
    DatabaseReference myStateRef;
    String clickedItemName;
    ArrayList<String> exams = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    String getYear;
    boolean finalGuest;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityStateExamsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        stateDetail = getIntent().getStringExtra("stateName");
        finalGuest = getIntent().getBooleanExtra("isGuest", false);

        myStateRef = FirebaseDatabase.getInstance().getReference("PDF");

        setTitle(""+stateDetail+" Exams");

        getDataFromFirebase();

        clickSetUp();
    }

    private void clickSetUp() {
        b.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                options.removeAll(options);
                if (finalGuest == true) {
                    ChooseGuestDialogBinding binding = ChooseGuestDialogBinding.inflate(LayoutInflater.from(StateExams.this));

                    new MaterialAlertDialogBuilder(StateExams.this,R.style.CustomDialogTheme2)
                            .setView(binding.getRoot())
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }

                else {
                    ChooseExamDialogBinding binding = ChooseExamDialogBinding.inflate(LayoutInflater.from(StateExams.this));
                    binding.chooseExam.setText("" + b.list.getItemAtPosition(position).toString());
                    new MaterialAlertDialogBuilder(StateExams.this, R.style.CustomDialogTheme)
                            .setView(binding.getRoot()).show();

                    binding.syllabus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkingConnectivity();
                            if(connected) {
                                Intent intent = new Intent(StateExams.this, StatesSyllabusActivity.class);
                                intent.putExtra("stateName", stateDetail);
                                intent.putExtra("examName", b.list.getItemAtPosition(position).toString());
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(StateExams.this, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    binding.notes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM") ||
                                    b.list.getItemAtPosition(position).toString().equals("TEACHER's ELIGIBILITY TEST")) {
                                Intent intent = new Intent(StateExams.this, StatesNotesActivity.class);
                                intent.putExtra("stateName", stateDetail);
                                intent.putExtra("examName", b.list.getItemAtPosition(position).toString());
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(StateExams.this, ComingSoon.class);
                                startActivity(intent);
                            }
                        }
                    });
                    binding.allInPrevPapers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b.list.getItemAtPosition(position).toString().equals("PUBLIC SERVICE COMMISSION EXAM")){
                                statePrelimsMethod(position, binding);
                            }

                            else if(stateDetail.equals("Andhra Pradesh") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Bihar") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Delhi") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, binding);
                            }
                            else if(stateDetail.equals("Gujarat") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, binding);
                            }
                            else if(stateDetail.equals("Haryana") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Jammu & Kashmir") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Jharkhand") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Karnataka") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Kerala") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Madhya Pradesh") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Maharashtra") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, binding);
                            }
                            else if(stateDetail.equals("Meghalaya") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, binding);
                            }
                            else if(stateDetail.equals("Nagaland") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Odisha") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Telangana") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("Uttarakhand") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePoliceMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else if(stateDetail.equals("West Bengal") &&
                                    b.list.getItemAtPosition(position).toString().equals("POLICE DEPARTMENT EXAM")){
                                statePrelimsMethod(position, (ChooseExamDialogBinding) binding);
                            }
                            else {
                                binding.group.setVisibility(View.GONE);
                                binding.group2.setVisibility(View.VISIBLE);
                                clickedItemName = b.list.getItemAtPosition(position).toString();

                                statePrevDatabase();
                                yearAdapter(binding);
                                moveToStatePrevY(binding);
                            }

                        }
                    });

                }
            }
        });
    }

    private void statePoliceMethod(int position, ChooseExamDialogBinding binding) {
        binding.group.setVisibility(View.GONE);
        binding.group7.setVisibility(View.VISIBLE);

        binding.subInspector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.group7.setVisibility(View.GONE);
                binding.group2.setVisibility(View.VISIBLE);
                clickedItemName = b.list.getItemAtPosition(position).toString();
                statePrevDatabasePolice();
                yearAdapter(binding);
                moveToStatePrevY(binding);
            }
        });
        binding.constable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StateExams.this, ComingSoon.class);
                startActivity(intent);
            }
        });
    }

    private void statePrevDatabasePolice() {
        myStateRef = FirebaseDatabase.getInstance().getReference("PDF");

        myStateRef.child(stateDetail).child(clickedItemName).child("subInspector").child("prevPaper").addValueEventListener
                (new ValueEventListener() {
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

    private void statePrelimsMethod(int position, ChooseExamDialogBinding binding) {
        binding.group.setVisibility(View.GONE);
        binding.group4.setVisibility(View.VISIBLE);

        binding.prelims.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.group4.setVisibility(View.GONE);
                binding.group2.setVisibility(View.VISIBLE);
                clickedItemName = b.list.getItemAtPosition(position).toString();
                statePrevDatabasePre();
                yearAdapter(binding);
                moveToStatePrevY(binding);
            }
        });
        binding.mainsPolitics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StateExams.this, ComingSoon.class);
                startActivity(intent);
            }
        });
    }

    private void moveToStatePrevY(ChooseExamDialogBinding binding) {
        binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                getYear = (String)parent.getItemAtPosition(position);
                binding.autoCompleteYear.setText(getYear,false);

                options.removeAll(options);
                binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkingConnectivity();
                        if(connected) {
                            Intent intent = new Intent(StateExams.this,
                                    StatePrevYearPaper.class);
                            intent.putExtra("stateName", stateDetail);
                            intent.putExtra("examName", clickedItemName);
                            intent.putExtra("year", getYear);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(StateExams.this, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void yearAdapter(ChooseExamDialogBinding binding) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(StateExams.this,R.layout.drop_down_view,options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.autoCompleteYear.setAdapter(arrayAdapter);
    }

    private void statePrevDatabase() {
        myStateRef = FirebaseDatabase.getInstance().getReference("PDF");

        myStateRef.child(stateDetail).child(clickedItemName).child("prevPaper").addValueEventListener
                (new ValueEventListener() {
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

    private void statePrevDatabasePre() {
        myStateRef = FirebaseDatabase.getInstance().getReference("PDF");

        myStateRef.child(stateDetail).child(clickedItemName).child("prelims").child("prevPaper").addValueEventListener
                (new ValueEventListener() {
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

    private void adapterForStates() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.drop_down_view,exams);
        b.list.setAdapter(arrayAdapter);
    }

    private void getDataFromFirebase() {
        myStateRef.child(stateDetail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String value = dataSnapshot.getKey().toString();
                    exams.add(value);
                }
                adapterForStates();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(StateExams.this, "F", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(StateExams.this.CONNECTIVITY_SERVICE);
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