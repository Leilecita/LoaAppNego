package com.example.android.loa.types;

public enum MoneyMovementType {

    ALL(Constants.TYPE_ALL),
    SANTI_PAGO_SUELDO(Constants.MONEY_SANTI_PAGO_SUELDO),
    SANTI_PAGO_MERCADERIA(Constants.MONEY_SANTI_PAGO_MERCADERIA),
    SANTI_PAGO_AUTONOMOS(Constants.MONEY_SANTI_PAGO_AUTONOMOS),
    SANTI_PAGO_ALQUILER(Constants.MONEY_SANTI_PAGO_ALQUILER),
    SANTI_PAGO_MIOS(Constants.MONEY_SANTI_PAGO_MIOS),
    SANTI_PAGO_CONTADOR(Constants.MONEY_SANTI_PAGO_CONTADOR);

    private final String name;

    MoneyMovementType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
