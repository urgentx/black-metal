package com.urgentx.blackmetal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Barco on 27-May-16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int numTabs;
    private FragmentManager fragmentManager;

    public PagerAdapter(FragmentManager fragmentManager, int numTabs){
        super(fragmentManager);
        this.numTabs = numTabs;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position){

        switch (position){
            case 0:
                MainFragment tab1 = new MainFragment();
                fragmentManager.beginTransaction().add(tab1,"mainFragID");
                return tab1;
            case 1:
                SettingsFragment tab2 = new SettingsFragment();

                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return numTabs;
    }
}
