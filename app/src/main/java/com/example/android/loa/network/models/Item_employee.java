package com.example.android.loa.network.models;

public class Item_employee {

    public Long id;
    public Long employee_id;
    public Double time_worked;
    public String turn;
    public String date;
    public String observation;

    public Item_employee(Long employee_id, Double time_worked, String turn, String date,String observation){

        this.employee_id=employee_id;
        this.time_worked=time_worked;
        this.turn=turn;
        this.date=date;
        this.observation=observation;
    }

}
