package com.example.android.loa.network.models;

public class Extraction {


    public Long id;
    public String description;
    public String type;
    public Double value;
    public String created;

    public Extraction(String description, String type, Double value){

        this.description=description;
        this.type=type;
        this.value=value;
    }
}
