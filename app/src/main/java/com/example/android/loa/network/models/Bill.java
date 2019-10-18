package com.example.android.loa.network.models;

public class Bill {

    public Long id;
    public String brand_name;
    public String number;
    public String created;

    public Bill(Long id,String brand,String number,String created) {
        this.brand_name=brand;
        this.created=created;
        this.id=id;
        this.number=number;
    }

}
