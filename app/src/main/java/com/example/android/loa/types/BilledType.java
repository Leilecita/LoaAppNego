package com.example.android.loa.types;

public enum BilledType {

    ALL(Constants.TYPE_ALL),
    FACTURADO(Constants.TYPE_FACTURA),
    SIN_FACTURAR(Constants.TYPE_REMITO);

    private final String name;

    BilledType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
