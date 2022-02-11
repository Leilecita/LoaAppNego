package com.example.android.loa.types;

public enum BillingType {

    ALL(Constants.TYPE_ALL),
    ARQ(Constants.TYPE_BILLING_ARQ),
    ESCUELITA(Constants.TYPE_BILLING_ESCUELITA),
    VARIOS(Constants.TYPE_BILLING_VARIOS);

    private final String name;

    BillingType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
