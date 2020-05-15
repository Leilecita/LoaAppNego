package com.example.android.loa.network.models;

public class Extraction {


    public Long id;
    public String description;
    public String type;
    public Double value;
    public String created,detail;

    public Extraction(String description, String type, Double value,String detail){

        this.description=description;
        this.detail=detail;
        this.type=type;
        this.value=value;
    }
}
