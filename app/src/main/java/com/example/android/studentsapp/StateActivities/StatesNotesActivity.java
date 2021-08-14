package com.example.android.studentsapp.StateActivities;

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

import com.example.android.studentsapp.BranchActivity;
import com.example.android.studentsapp.R;
import com.example.android.studentsapp.databinding.ActivityStatesNotesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StatesNotesActivity extends AppCompatActivity {

    ActivityStatesNotesBinding b;
    private DatabaseReference mySubjectRef;
    ArrayList<String> options = new ArrayList<>();
    ArrayList<String> topic = new ArrayList<>();
    String examDetail;
    String stateDetail;
    String getSubject;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityStatesNotesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        stateDetail = getIntent().getStringExtra("stateName");
        examDetail = getIntent().getStringExtra("examName");

        getDataFromFirebase();
        adapterForSubject();
        getTopicsFromFirebase();
        adapterForTopics();
    }

    private void getDataFromFirebase() {
        mySubjectRef = FirebaseDatabase.getInstance().getReference("PDF");
        mySubjectRef.child(stateDetail).child(examDetail).child("notes").addValueEventListener
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

    private void adapterForSubject() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.drop_down_view,options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteTextView2.setAdapter(arrayAdapter);
    }

    private void adapterForTopics() {
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this,R.layout.drop_down_view,topic);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b.autoCompleteTopics.setAdapter(arrayAdapter2);
    }

    private void getTopicsFromFirebase() {
        b.autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                topic.removeAll(topic);
                b.textInputLayout1.setVisibility(View.VISIBLE);
                getSubject = (String)parent.getItemAtPosition(position);
                b.autoCompleteTextView2.setText(getSubject,false);

                mySubjectRef.child(stateDetail).child(examDetail).child("notes").child(getSubject)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String value2 = dataSnapshot.getKey().toString();
                                    topic.add(value2);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    public void moveToStateNotesPdfPlease(View view) {
        if(b.autoCompleteTextView2.getText().toString().trim().equals("Subject")){
            Toast.makeText(this, "Please choose subject!", Toast.LENGTH_LONG).show();
            return;
        }

        if(b.autoCompleteTopics.getEditableText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please choose topic!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!topic.contains(b.autoCompleteTopics.getEditableText().toString().trim())){
            Toast.makeText(this, "Invalid topic name!", Toast.LENGTH_LONG).show();
            return;
        }

        checkingConnectivity();
        if(connected) {
            Intent intent = new Intent(StatesNotesActivity.this, StateNotesPDF.class);
            intent.putExtra("stateName", stateDetail);
            intent.putExtra("examName", examDetail);
            intent.putExtra("subjectName", getSubject);
            intent.putExtra("topicName", b.autoCompleteTopics.getEditableText().toString().trim());
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(StatesNotesActivity.this.CONNECTIVITY_SERVICE);
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