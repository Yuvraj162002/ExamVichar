package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class PrevIndiaDesc implements Serializable {
    public String examName;
    public String year;
    public String url;

    public PrevIndiaDesc() {
    }

    public PrevIndiaDesc(String examName, String year, String url) {
        this.examName = examName;
        this.year = year;
        this.url = url;
    }
}

