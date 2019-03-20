package com.example.android.loa.network.models;

public class Item_employee {

    public Long id;
    public Long employee_id;
    public Double time_worked;
    public String turn;
    public String entry;
    public String finish;
    public String date;
    public String observation;
    public String created;


    public Double time_worked_aft;
    public String entry_aft;
    public String finish_aft;
    public String obs_aft;

    public Item_employee(Long employee_id, Double time_worked, String turn, String date,String observation,String entry,String finish,
                         Double time_worked_aft, String obs_aft,String entry_aft,String finish_aft
    ){

        this.employee_id=employee_id;
        this.time_worked=time_worked;
        this.turn=turn;
        this.entry=entry;
        this.finish=finish;
        this.date=date;
        this.observation=observation;

        this.time_worked_aft=time_worked_aft;
        this.obs_aft=obs_aft;
        this.finish_aft=finish_aft;
        this.entry_aft=entry_aft;
    }

}
