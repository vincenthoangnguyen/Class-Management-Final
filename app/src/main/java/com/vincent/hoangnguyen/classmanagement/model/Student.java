package com.vincent.hoangnguyen.classmanagement.model;

import com.google.firebase.Timestamp;

public class Student {
    private String name;
    private String id;
    private String phoneNumber;
    private String email;
    private com.google.firebase.Timestamp timestamp;
    private String midtermScore,finalScore;
    public Student() {
    }

    public String getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(String midtermScore) {
        this.midtermScore = midtermScore;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
