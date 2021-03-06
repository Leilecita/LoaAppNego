package com.example.android.loa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.loa.fragments.BaseFragment;
import com.example.android.loa.fragments.ClientsFragment;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<BaseFragment> mFragments;

    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();
        mFragments.add(new ClientsFragment());

        // mFragments.add(new PreimpresoFragment().setChangeListener(this));
        // mFragments.add(new MistakeFragment());
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /*  @Override
      public void onChange(Fragment fragment) {
          notifyDataSetChanged();
      }
*/
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0){
            return "Deudas";
        }else{
            return "Deudas";
        }
       /* if (position == 0) {
            return "Clientes";
        }else if(position == 1){
            return"Pedidos";

        }else{
            return"Resumen";
        }*/
    }
}