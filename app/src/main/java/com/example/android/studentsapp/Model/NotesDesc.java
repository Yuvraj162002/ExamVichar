package com.example.android.studentsapp.Model;

import java.io.Serializable;

public class NotesDesc implements Serializable {

        public String examName;
        public String subject;
        public String topic;
        public String url;

        public NotesDesc() {
        }

        public NotesDesc(String examName, String subject, String topic, String url) {
            this.examName = examName;
            this.subject = subject;
            this.topic = topic;
            this.url = url;
        }
    }


