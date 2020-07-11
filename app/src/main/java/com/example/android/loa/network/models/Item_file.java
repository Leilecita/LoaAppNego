package com.example.android.loa.network.models;

public class Item_file {
    public Long id;
    public Long client_file_id;
    public Long client_id;
    public String description;
    public String observation;
    public String product_kind;
    public String brand;
    public String product_type;
    public String product_model;
    public String size;
    public String code;
    public String settled;
    public Double value;
    public Double previous_balance;
    public String modify_by,payment_method;
    public String created;
    public String retired_product;
    public String balance;

    public Item_file(){}

    public Item_file(Long client_file_id, Long client_id,String description, Double value,Double previous_balance,String observation,
    String brand,String code,String size,String product_kind,String settled,String payment_method,String retired_product){
        this.client_file_id=client_file_id;
        this.client_id=client_id;
        this.description=description;
        this.product_kind=product_kind;
        this.size=size;
        this.brand=brand;
        this.code=code;
        this.value=value;
        this.observation=observation;
        this.settled=settled;
        this.previous_balance=previous_balance;
        this.payment_method=payment_method;
        this.retired_product=retired_product;
    }
}
