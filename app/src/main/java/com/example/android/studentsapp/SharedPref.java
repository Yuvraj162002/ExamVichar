package com.example.android.studentsapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref ;
    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename",0);
    }
    // this method will save the nightMode State : True or False
    public void setNightModeState(int state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt("NightModeInteger",state);
        editor.apply();
    }
    // this method will load the Night Mode State
    public Integer loadNightModeState (){
        int state = mySharedPref.getInt("NightModeInteger",3);
        return  state;
    }
}
