package com.example.android.loa.types;

public enum ExtractionType {

        ALL(Constants.TYPE_ALL),
        GASTO_PERSONAL(Constants.TYPE_GASTO_PERSONAL),
        GASTO_SANTI(Constants.TYPE_GASTO_SANTI),
        GASTO_LOCAL(Constants.TYPE_GASTO_LOCAL),
        SANTI(Constants.TYPE_SANTI),
        MERCADERIA(Constants.TYPE_MERCADERIA),
        SUELDO(Constants.TYPE_SUELDO),
        OTRO(Constants.TYPE_OTRO);

        private final String name;

        ExtractionType(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }


}
