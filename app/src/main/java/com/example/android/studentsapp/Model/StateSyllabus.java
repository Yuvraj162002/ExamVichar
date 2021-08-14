package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class StateSyllabus implements Serializable {
    public String stateName;
    public String examName;
    public String url;

    public StateSyllabus() {
    }

    public StateSyllabus(String stateName, String examName, String url) {
        this.stateName = stateName;
        this.examName = examName;
        this.url = url;
    }
}
