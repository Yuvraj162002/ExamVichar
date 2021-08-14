package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class PrevStatesDesc implements Serializable {
    public String stateName;
    public String examName;
    public String year;
    public String url;

    public PrevStatesDesc() {
    }

    public PrevStatesDesc(String stateName, String examName, String year, String url) {
        this.stateName = stateName;
        this.examName = examName;
        this.year = year;
        this.url = url;
    }
}
