package com.example.android.loa.fragments;

import android.support.v4.app.Fragment;

import com.example.android.loa.Interfaces.OnFloatingButton;
import com.example.android.loa.R;

public class BaseFragment extends Fragment implements OnFloatingButton {


    public void onClickButton(){}


    public int getIconButton(){
        return R.drawable.ic_launcher_background;
    }

    public int getVisibility(){return 0;}

}
