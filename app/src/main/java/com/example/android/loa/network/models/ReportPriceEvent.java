package com.example.android.loa.network.models;

public class ReportPriceEvent {

    public Long id;
    public String user_name;
    public String brand;
    public String item;
    public String type;
    public String model;
    public String created;

    public Double previous_price;
    public Double actual_price;

    public ReportPriceEvent(){

    }
}
