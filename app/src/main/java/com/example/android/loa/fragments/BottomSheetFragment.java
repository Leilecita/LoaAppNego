package com.example.android.loa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.android.loa.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v= inflater.inflate(R.layout.bottom_shet, container, false);

        LinearLayout lin_man= v.findViewById(R.id.man);
        LinearLayout lin_woman= v.findViewById(R.id.woman);

        lin_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Man");
                dismiss();
            }
        });

        lin_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Woman");
                dismiss();
            }
        });


        return v;
    }

    public interface BottomSheetListener{

        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener= (BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ "must implement BottomSheetListener");
        }

    }
}