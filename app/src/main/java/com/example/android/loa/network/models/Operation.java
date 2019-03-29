package com.example.android.loa.network.models;

public class Operation {
    public Long item_file_id;
    public String name;
    public String description;
    public String observation;
    public String product_kind;
    public String brand;
    public String size;
    public String code;
    public String settled;
    public Double value;
    public Double previous_balance;
    public String modify_by;
    public String created;


    public Long client_id;



    public Operation(String name, String description, Double value,Double previous_balance, String created, Long item_file_id,String obs,String settled
    ,String product_kind,String brand, String size, String code,Long client_id){
        this.client_id=client_id;
        this.name=name;
        this.description=description;
        this.value=value;
        this.created=created;
        this.observation=obs;
        this.product_kind=product_kind;
        this.size=size;
        this.brand=brand;
        this.code=code;
        this.item_file_id=item_file_id;
        this.settled=settled;
        this.previous_balance=previous_balance;
    }
}
