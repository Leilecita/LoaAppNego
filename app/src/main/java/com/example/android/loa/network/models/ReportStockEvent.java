package com.example.android.loa.network.models;

public class ReportStockEvent {

    public Long stock_event_id;
    public Integer stock_in; //ingresos
    public Integer stock_out;//ventas
    public String item;
    public String type;
    public String brand;
    public String model;
    public String stock_event_created;
    public Double value;
    public String payment_method;
    public String detail;
    public String client_name;
    public String observation;

    public ReportStockEvent(){}
}
