package com.example.android.loa.network.models;

public class Box {

    public Long id;

    public Double counted_sale;
    public Double credit_card;
    public Double total_box;
    public Double rest_box;
    public Double deposit;
    public String detail;
    public String created;

    public Box( Double counted_sale, Double credit_card,Double total_box, Double rest_box,
                Double deposit, String detail){

        this.counted_sale=counted_sale;
        this.credit_card=credit_card;
        this.total_box=total_box;
        this.rest_box=rest_box;
        this.deposit=deposit;
        this.detail=detail;
    }
}
