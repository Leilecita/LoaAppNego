package com.example.android.loa.network.models;

public class Product {

    public Long id;
    public String item;
    public String type;
    public String brand;
    public String created;
    public Integer stock;

    public Product(String item,String type, String brand, Integer stock){

        this.item=item;
        this.brand=brand;
        this.stock=stock;
        this.type=type;
    }
}
