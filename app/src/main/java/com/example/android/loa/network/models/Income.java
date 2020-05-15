package com.example.android.loa.network.models;

public class Income {

    public Long id;
    public String description,payment_method;
    public String type;
    public Double value;
    public String created;
    public String retired_product;

    public String name,phone,address;
    public Double value_product;

    public Income(String description, String type, Double value,String payment_method,String retired_product,
                  String name, String phone, String address, Double value_product){

        this.description=description;
        this.payment_method=payment_method;
        this.type=type;
        this.value=value;
        this.retired_product=retired_product;

        this.name=name;
        this.phone=phone;
        this.address=address;
        this.value_product=value_product;
    }
}

