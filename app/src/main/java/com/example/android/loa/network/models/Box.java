package com.example.android.loa.network.models;

public class Box {

    public Long id;

    public Double counted_sale;
    public Double credit_card;
    public Double total_box;
    public Double rest_box;
    public Double deposit;
    public String detail;
    public String image_url;
    public String image_url_posnet;
    public String created;

    public String imageData;
    public String imageDataPosnet;

    public Double rest_box_ant;

    public Box( Double counted_sale, Double credit_card,Double total_box, Double rest_box,
                Double deposit, String detail,String image_box_url,String image_url_posnet,Double rest_box_ant){

        this.counted_sale=counted_sale;
        this.credit_card=credit_card;
        this.total_box=total_box;
        this.rest_box=rest_box;
        this.deposit=deposit;
        this.detail=detail;
        this.image_url=image_box_url;
        this.image_url_posnet=image_url_posnet;
        this.rest_box_ant=rest_box_ant;
    }
}
