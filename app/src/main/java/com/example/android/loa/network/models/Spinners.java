package com.example.android.loa.network.models;

import java.util.List;

public class Spinners {

    public List<SpinnerItem> items;
    public List<SpinnerData> brands;
    public List<SpinnerType> types;
    public List<SpinnerModel> models;

    public Spinners(List<SpinnerItem> items, List<SpinnerData> brands, List<SpinnerType> types, List<SpinnerModel> models){

        this.brands=brands;
        this.types=types;
        this.items=items;
        this.models=models;
    }
}
