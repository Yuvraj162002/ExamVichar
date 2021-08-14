package com.example.android.studentsapp.IndianActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.studentsapp.Adapters.SadaAdapter;
import com.example.android.studentsapp.Model.ExamDates;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.NetWorkActivity;
import com.example.android.studentsapp.databinding.ActivityIndianBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IndianActivity extends AppCompatActivity {

    ActivityIndianBinding b;
    RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<ExamDates> examDatesArrayList;
    SadaAdapter adapter;
    MyLoaders myLoaders;
    public boolean isFirstGuest;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityIndianBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        myLoaders = new MyLoaders();

        checkingConnectivity();
        descisionMaking();

    }

    private void descisionMaking() {
        if(connected){
            initialisingActivity();
        }
        else{
            Intent intent = new Intent(IndianActivity.this, NetWorkActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(IndianActivity.this.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }

    private void initialisingActivity() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(6);

        isFirstGuest = getIntent().getBooleanExtra("isGuest", false);

        myRef = FirebaseDatabase.getInstance().getReference("ImageLoader");
        examDatesArrayList = new ArrayList<>();

        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        myLoaders.settingUpLoader(IndianActivity.this);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExamDates value = dataSnapshot.getValue(ExamDates.class);
                    examDatesArrayList.add(value);
                    setUpDates();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(IndianActivity.this, "Failed to retrieve!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpDates() {
        adapter = new SadaAdapter(IndianActivity.this, examDatesArrayList, isFirstGuest);
        b.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myLoaders.hideLoadingDialog();
    }
}