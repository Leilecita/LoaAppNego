package com.example.android.loa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.loa.fragments.BaseFragment;
import com.example.android.loa.fragments.BoxFragment;
import com.example.android.loa.fragments.ClientsFragment;
import com.example.android.loa.fragments.EmployeesFragment;
import com.example.android.loa.fragments.ExtractionsFragment;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<BaseFragment> mFragments;

    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();
        mFragments.add(new ClientsFragment());
        mFragments.add(new EmployeesFragment());
        mFragments.add(new ExtractionsFragment());
        mFragments.add(new BoxFragment());

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
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0){
            return "Deudas";
        }else if(position==1){
            return "Team";
        }else if(position ==2){
            return "Extrac";
        }else{
            return "Cajas";
        }
    }
}