package com.example.asg01.entity;

import java.io.Serializable;

public class User implements Serializable {
    private String fullname;
    private String birthday;
    private int score = 0;
    private String phoneNumber;
    private String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public User() {
    }

    public User(String fullname, String birthday, String phoneNumber, String position) {
        this.fullname = fullname;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.position = position;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
