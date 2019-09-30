package com.example.android.loa.network.models;


public class LoginBody {
    private String name;
    private String hash_password;

    public LoginBody(String name, String password) {
        this.name = name;
        this.hash_password = password;
    }

    public String getPassword() {
        return hash_password;
    }

    public void setPassword(String password) {
        this.hash_password = password;
    }
}
