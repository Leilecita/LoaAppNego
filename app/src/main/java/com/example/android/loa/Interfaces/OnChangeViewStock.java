package com.example.android.loa.Interfaces;

import com.example.android.loa.network.models.Product;

public interface OnChangeViewStock {
    void OnChangeViewStock();

    void onReloadTotalQuantityStock();

    void scrollToPosition(Integer pos);

  //  void changeEnableSwipRefresh();
}
