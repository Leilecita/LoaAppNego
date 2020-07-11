package com.example.android.loa.network.models;

public class GeneralStock {


        public Long id;

        public String item;
        public String type;
        public Integer stock;
        public String result;
        public Integer difference;
        public String created;

        public GeneralStock(String item, String type, Integer stock, String result,Integer dif){
                this.item=item;
                this.type=type;
                this.stock=stock;
                this.result=result;
                this.difference=dif;
        }


}
