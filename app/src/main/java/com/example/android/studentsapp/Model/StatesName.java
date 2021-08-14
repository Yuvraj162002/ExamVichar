package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class StatesName implements Serializable {

    public String stateName;
    public String url;


    public StatesName() {
    }

    public StatesName(String stateName, String url) {
        this.stateName = stateName;
        this.url = url;
    }

}
