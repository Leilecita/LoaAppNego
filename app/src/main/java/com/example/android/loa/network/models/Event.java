package com.example.android.loa.network.models;

public class Event {

    public Long id;

    public String description;
    public String state;
    public Double value;
    public Double previous_value;
    public String employee_name;
    public String previous;
    public String created;

    public Event(String description,String previous, Double value,Double previous_value, String employee_name,String state){
        this.previous=previous;
        this.previous_value=previous_value;
        this.description=description;
        this.state=state;
        this.value=value;
        this.employee_name=employee_name;
    }

}
