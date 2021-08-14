package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.studentsapp.IndianActivities.IndiaPrevYearPapers;
import com.example.android.studentsapp.StateActivities.StateExams;
import com.example.android.studentsapp.databinding.ActivityMeritPapersBinding;
import com.example.android.studentsapp.databinding.ActivityStateExamsBinding;
import com.example.android.studentsapp.databinding.ChooseExamDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MeritPapers extends AppCompatActivity {

    DatabaseReference myRef;
    String examName;
    String year;
    String subExam;
    String subSubExam;
    String subSubSubExam;
    ArrayList<String> exams = new ArrayList<>();
    ArrayList<String> subExams = new ArrayList<>();
    ActivityMeritPapersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeritPapersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myRef = FirebaseDatabase.getInstance().getReference("PDF");

        examName = getIntent().getStringExtra("examName");
        year = getIntent().getStringExtra("year");
        subExam = getIntent().getStringExtra("subExamName");
        subSubExam = getIntent().getStringExtra("subsubExamName");
        subSubSubExam = getIntent().getStringExtra("subsubsubExam");

        getDataFromFirebase();

        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positions, long id) {
                ChooseExamDialogBinding b = ChooseExamDialogBinding.inflate(LayoutInflater.from(MeritPapers.this));
                b.chooseExam.setText("" + binding.list.getItemAtPosition(positions).toString());
                new MaterialAlertDialogBuilder(MeritPapers.this, R.style.CustomDialogTheme)
                        .setView(b.getRoot()).show();
                b.group.setVisibility(View.GONE);
                b.group2.setVisibility(View.VISIBLE);
                myRef.child(examName).child(subExam).child(subSubExam).child(subSubSubExam).
                        child(binding.list.getItemAtPosition(positions).toString()).child("prevPaper").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String value = dataSnapshot.getKey().toString();
                                    subExams.add(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Toast.makeText(MeritPapers.this, "F", Toast.LENGTH_SHORT).show();
                            }
                        });

                ArrayAdapter arrayAdapter = new ArrayAdapter(MeritPapers.this, R.layout.drop_down_view, subExams);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                b.autoCompleteYear.setAdapter(arrayAdapter);

                b.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parents, View view, int position, long id) {
                        b.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                year =  parents.getItemAtPosition(position).toString();
                                Toast.makeText(MeritPapers.this,  binding.list.getItemAtPosition(positions).toString(), Toast.LENGTH_SHORT).show();
                                b.autoCompleteYear.setText(year, false);
                                subExams.removeAll(subExams);
                                Intent intent = new Intent(MeritPapers.this, IndiaPrevYearPapers.class);
                                intent.putExtra("examName", examName);
                                intent.putExtra("year", year);
                                intent.putExtra("subExamName", "mains");
                                intent.putExtra("subsubExamName","passMerit");
                                intent.putExtra("subsubsubExam","optional");
                                intent.putExtra("subsubsubsubExam", binding.list.getItemAtPosition(positions).toString());
                                startActivity(intent);
                            }
                        });
                    }
                });


            }
        });
    }


    private void getDataFromFirebase() {
        myRef.child(examName).child(subExam).child(subSubExam).child(subSubSubExam).
                addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(MeritPapers.this, "F", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void adapterForStates() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.drop_down_view,exams);
        binding.list.setAdapter(arrayAdapter);
    }
}