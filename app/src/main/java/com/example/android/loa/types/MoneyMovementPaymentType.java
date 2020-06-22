package com.example.android.loa.types;

public enum MoneyMovementPaymentType {
    ALL(Constants.TYPE_ALL),
    TRANSFERENCIA(Constants.TYPE_TRANSFERENCIA),
    EFECTIVO(Constants.TYPE_EFECTIVO),
    CHEQUE(Constants.TYPE_CHEQUE);

    private final String name;

     MoneyMovementPaymentType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
