package com.example.android.loa.network.models;

public class StockEvent {

    public Long id;
    public Long id_product;
    public Integer stock_in; //ingresos
    public Integer stock_out;//ventas
    public Integer stock_ant;//stock anterior
    public Integer ideal_stock; //stock que deberia haber
    public Integer balance_stock;  //balance real a la fecha
    public String detail;
    public String created;
    public Double value;
    public String payment_method;
    public String client_name;
    public String today_created_client;

    public Long client_id=-1l;
    public Double value_for_file=0.0;

    public String observation;

    public StockEvent(Long product_id,Integer stock_in, Integer stock_out,Integer stock_ant,String detail,Double value,String payment_method,String obs){
        this.stock_in=stock_in;
        this.stock_out=stock_out;
        this.stock_ant=stock_ant;
        this.detail=detail;
        this.id_product=product_id;
        this.value=value;
        this.payment_method=payment_method;
        this.observation=obs;

        this.today_created_client="false";
    }

}
