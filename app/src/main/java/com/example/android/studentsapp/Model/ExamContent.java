package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class ExamContent implements Serializable {
    public String examName;
    public String selectType;
    public String pdfName;
    public String url;

    public ExamContent(){

    }

    public ExamContent(String examName, String selectType, String pdfName, String url) {
        this.examName = examName;
        this.selectType = selectType;
        this.pdfName = pdfName;
        this.url = url;
    }

}
