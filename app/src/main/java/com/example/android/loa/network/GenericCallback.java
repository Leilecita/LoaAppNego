package com.example.android.loa.network;

public interface GenericCallback<T> {
    void onSuccess(T data);
    void onError(Error error);
}
