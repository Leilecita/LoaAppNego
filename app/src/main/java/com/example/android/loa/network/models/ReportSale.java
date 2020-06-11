package com.example.android.loa.network.models;

import java.util.ArrayList;

public class ReportSale {

    public String created;
    public ArrayList<ReportStockEvent> listStockEventSale;
    public ArrayList<ReportItemFileClientEvent> listItems;
    public Double efectAmount;
    public Double cardAmount;
    public Double transfAmount;
    public Double mercPagoAmount;
    public Integer countSales;

    public ReportSale(){}

}
