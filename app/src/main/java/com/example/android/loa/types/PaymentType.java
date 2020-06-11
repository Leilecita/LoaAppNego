package com.example.android.loa.types;

public enum PaymentType {

    DEBITO(Constants.TYPE_DEBITO),
    CREDITO(Constants.TYPE_TARJETA),
    EFECTIVO(Constants.TYPE_EFECTIVO),
    MERCADO_PAGO(Constants.TYPE_MERCADO_PAGO),
    TRANSFERENCIA(Constants.TYPE_TRANSFERENCIA);

    private final String name;

    PaymentType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
