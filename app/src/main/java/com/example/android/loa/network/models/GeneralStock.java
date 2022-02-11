package com.example.android.loa.network.models;

public class GeneralStock {


        public Long id;

        public String item;
        public String type;
        public Integer ideal_stock;
        public Integer real_stock;
        public String result;
        public Integer difference;
        public String created;

        public String brand;

        public GeneralStock(String item, String type, Integer ideal_stock,Integer real_stock, String result,Integer dif){
                this.item=item;
                this.type=type;
                this.ideal_stock=ideal_stock;
                this.real_stock=real_stock;
                this.result=result;
                this.difference=dif;
        }


}
