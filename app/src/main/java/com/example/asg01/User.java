package com.example.asg01;

public class User {
    private String email;
    private String password;
    private String fullname;
    private String birthday;

    public User() {
    }

    public User(String email, String password, String fullname, String birthday) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}
