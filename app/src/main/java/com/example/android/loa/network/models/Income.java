package com.example.android.loa.network.models;

public class Income {

    public Long id;

    public String name,phone,address,description,payment_method,state;
    public Double value;
    public Double value_product;

    public String retired_product;
    public String created;

    public Income(String description, Double value,String payment_method,String retired_product,
                  String name, String phone, String address, Double value_product){

        this.description=description;
        this.payment_method=payment_method;
        this.value=value;
        this.retired_product=retired_product;

        this.name=name;
        this.phone=phone;
        this.address=address;
        this.value_product=value_product;
        this.state="pendient";
    }
}

