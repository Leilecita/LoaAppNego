package com.example.android.loa.types;

public enum GroupByType {

    MONTH(Constants.TYPE_GROUP_BY_MONTH),
    DAY(Constants.TYPE_GROUP_BY_DAY);

    private final String name;

    GroupByType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
