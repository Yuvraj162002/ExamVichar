package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class StatesDesc implements Serializable {
    public String stateName;
    public String examName;
    public String stateSubjectName;
    public String stateTopicName;
    public String url;

    public StatesDesc() {
    }

    public StatesDesc(String stateName, String examName, String stateSubjectName,
                                 String stateTopicName, String url) {
        this.stateName = stateName;
        this.examName = examName;
        this.stateSubjectName = stateSubjectName;
        this.stateTopicName = stateTopicName;
        this.url = url;
    }
}
