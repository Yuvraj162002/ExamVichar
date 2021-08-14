package com.example.android.studentsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.studentsapp.IndianActivities.IndianActivity;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.StateActivities.StatesActivity;

public class NetWorkActivity extends AppCompatActivity {

    boolean connected;
    String gettingIntent;
    MyLoaders myLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work);
        myLoaders = new MyLoaders();


    }


    public void sendingBackToMama(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(NetWorkActivity.this.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }

        if(connected){
            Intent output = new Intent(this, HomeActivity.class);
            startActivity(output);
            finish();
        }
        else{
            Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show();
        }
    }
}