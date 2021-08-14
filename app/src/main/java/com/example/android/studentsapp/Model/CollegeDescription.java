package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class CollegeDescription implements Serializable {
    public String courseName;
    public String typeName;
    public String branchName;
    public String stateTopicName;
    public String url;

    public CollegeDescription() {
    }

    public CollegeDescription(String courseName, String typeName, String branchName,
                              String stateTopicName, String url) {
        this.courseName = courseName;
        this.typeName = typeName;
        this.branchName = branchName;
        this.stateTopicName = stateTopicName;
        this.url = url;
    }
}
