package com.example.android.loa.network.models;

public class Product {

    public Long id;
    public String item;
    public String type;
    public String brand;
    public String model;
    public String deleted;
    public String created;
    public Integer stock;

    public Product(String item,String type, String brand,String model, Integer stock){

        this.item=item;
        this.brand=brand;
        this.model=model;
        this.stock=stock;
        this.type=type;
        this.deleted="false";
    }
}
