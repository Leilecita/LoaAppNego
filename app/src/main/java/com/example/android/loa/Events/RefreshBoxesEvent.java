package com.example.android.loa.Events;

public class RefreshBoxesEvent {

    public String mMessage;

    public RefreshBoxesEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}