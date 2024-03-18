package com.example.asg01;

import java.io.Serializable;

public class User implements Serializable {
    private String fullname;
    private String birthday;
    private int score = 0;

    public User() {
    }

    public User(String fullname, String birthday) {
        this.fullname = fullname;
        this.birthday = birthday;
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
}
