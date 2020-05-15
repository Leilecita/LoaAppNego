package com.example.android.loa.network.models;

import java.util.ArrayList;

public class ReportSale {

    public String created;
    public ArrayList<ReportStockEvent> listStockEventSale;
    public ArrayList<ReportItemFileClientEvent> listItems;
    public ArrayList<Income> listIncomes;
    public Double efectAmount;
    public Double cardAmount;
    public Integer countSales;

    public ReportSale(){}

}
