package com.example.android.loa.types;

public enum MoneyMovementPaymentType {
    EFECTIVO(Constants.TYPE_EFECTIVO),
    TRANSFERENCIA(Constants.TYPE_TRANSFERENCIA),

    MERCADO_PAGO(Constants.TYPE_MERCADO_PAGO),
    CHEQUE(Constants.TYPE_CHEQUE);

    private final String name;

     MoneyMovementPaymentType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
