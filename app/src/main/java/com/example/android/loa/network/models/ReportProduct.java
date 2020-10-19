package com.example.android.loa.network.models;

public class ReportProduct {

    public Long product_id;
    public Long event_price_id;
    public String item;
    public String type;
    public String brand;
    public String model;
    public String deleted;
    public String event_price_created;
    public Integer stock;

    public Double price;
    public Double actual_price;
    public Double previous_price;

    public boolean isSelected;

    public ReportProduct(){
        isSelected = false;
    }
}
