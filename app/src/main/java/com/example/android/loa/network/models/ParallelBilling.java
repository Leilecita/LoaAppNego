package com.example.android.loa.network.models;

public class ParallelBilling {

    public Long id;
    public String description, type,user_name;
    public Double amount;
    public String created;

    public ParallelBilling( Double amount,String type,String description,String user_name){

        this.type = type;
        this.user_name = user_name;
        this.description = description;
        this.amount = amount;
    }
}
