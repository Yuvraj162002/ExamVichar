package com.example.android.studentsapp.StateActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.studentsapp.Adapters.StatesAdapter;
import com.example.android.studentsapp.IndianActivities.IndianActivity;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.Model.StatesName;
import com.example.android.studentsapp.NetWorkActivity;
import com.example.android.studentsapp.databinding.ActivityStatesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StatesActivity extends AppCompatActivity {

    ActivityStatesBinding b;
    GridView gridView;
    private DatabaseReference myRef;
    private ArrayList<StatesName> stateNamesArrayList;
    StatesAdapter adapter;
    MyLoaders myLoaders;
    boolean isSecondGuest;
    boolean connected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityStatesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        myLoaders = new MyLoaders();
        checkingConnectivity();
        descisionMaking();

        initialisingActivity();
    }

    private void initialisingActivity() {
        isSecondGuest = getIntent().getBooleanExtra("isGuest", false);

        myRef = FirebaseDatabase.getInstance().getReference("StateImageLoader");
        stateNamesArrayList = new ArrayList<>();

        getDataFromFirebase();
    }

    private void descisionMaking() {
        if(connected){
            initialisingActivity();
        }
        else{
            Intent intent = new Intent(StatesActivity.this, NetWorkActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(StatesActivity.this.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }

    private void getDataFromFirebase() {
        myLoaders.settingUpLoader(StatesActivity.this);
        myRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StatesName value = dataSnapshot.getValue(StatesName.class);
                    stateNamesArrayList.add(value);
                }
                setUpDates();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(StatesActivity.this, "Failed to retrieve!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpDates() {
        adapter = new StatesAdapter(StatesActivity.this,stateNamesArrayList, isSecondGuest);
        b.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        b.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        myLoaders.hideLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        myLoaders.hideLoadingDialog();
        super.onDestroy();
    }
}