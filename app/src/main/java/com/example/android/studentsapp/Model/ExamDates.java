package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class ExamDates implements Serializable {
    public String examName;
    public String examDate;
    public String url;

    public ExamDates() {
    }

    public ExamDates(String examName, String examDate, String url) {
        this.examName = examName;
        this.examDate = examDate;
        this.url = url;
    }
}
