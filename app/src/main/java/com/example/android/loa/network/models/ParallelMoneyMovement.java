package com.example.android.loa.network.models;

public class ParallelMoneyMovement {

    public Long id;
    public String description;
    public String type;
    public Double value;
    public String created,detail,billed;

    public ParallelMoneyMovement(String description, String type, Double value,String detail,String billed){

        this.description=description;
        this.detail=detail;
        this.type=type;
        this.value=value;
        this.billed=billed;
    }
}
