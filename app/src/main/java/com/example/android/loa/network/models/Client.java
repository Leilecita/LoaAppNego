package com.example.android.loa.network.models;

public class Client {

    public Long id;
    public String name;
    public String address;
    public String phone;
    public String alternative_phone;
    public String employee_creator_id;
    public String image_url;
    public Double debt;

    public String imageData;

    public Client(String name, String address, String phone, String alternative_phone,String picpath,Double debt,String employee_creator_id){

        this.name=name;
        this.address=address;
        this.phone=phone;
        this.alternative_phone=alternative_phone;
        this.image_url=picpath;
        this.debt=debt;
        this.employee_creator_id=employee_creator_id;
    }

    public String getImage_url(){return this.image_url;}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAlternative_phone(String alternative_phone) {
        this.alternative_phone = alternative_phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getAlternative_phone() {
        return alternative_phone;
    }



}
