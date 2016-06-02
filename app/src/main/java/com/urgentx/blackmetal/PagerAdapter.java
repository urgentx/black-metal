package com.urgentx.blackmetal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Implementation of PagerAdapter to generate appropriate fragment to display in MainActivity.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numTabs;
    private FragmentManager fragmentManager;

    public PagerAdapter(FragmentManager fragmentManager, int numTabs){  //constructor
        super(fragmentManager);
        this.numTabs = numTabs; //could customise # of tabs here
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position){  //initiate appropriate fragment based on tab selected
        switch (position){
            case 0:
                MainFragment tab1 = new MainFragment();
                fragmentManager.beginTransaction().add(tab1,"mainFragID");  //give this fragment an ID so we can access it later
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
