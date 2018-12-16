package com.example.android.loa;

public class CurrentValuesHelper {


    private static CurrentValuesHelper INSTANCE = new CurrentValuesHelper();

    //OrdersFragment
    private String mOrderClientBy;

    public String getmOrderClientBy() {
        return mOrderClientBy;
    }

    public void setmOrderClientBy(String mOrderClientBy) {
        this.mOrderClientBy = mOrderClientBy;
    }

    private CurrentValuesHelper(){
        this.mOrderClientBy="name";
    }

    public static CurrentValuesHelper get(){
        return INSTANCE;
    }
}
