package com.example.android.loa.network.models;

public class BuyBilling {

        public Long id;
        public String number, type,business_name,iva;
        public Double amount;
        public Integer art_cant;
        public String user_name, billing_date, created;

        public BuyBilling(Double amount, String type, String business_name, String iva, Integer art_cant, String number, String user_name){

            this.type = type;
            this.art_cant = art_cant;
            this.amount = amount;
            this.business_name = business_name;
            this.iva = iva;
            this.number = number;
            this.user_name = user_name;
        }

}
