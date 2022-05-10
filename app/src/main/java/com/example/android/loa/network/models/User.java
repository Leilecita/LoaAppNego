package com.example.android.loa.network.models;

public class User {

    public Long id;
    public String name, category,hash_password,mail,phone,token;

    public User(String name, String password, String mail,String phone,String token){
        this.name=name;
        this.hash_password=password;
        this.mail=mail;
        this.phone=phone;
        this.token=token;
    }
}
