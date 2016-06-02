package com.urgentx.blackmetal;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Starting Activity, uses a ViewPager to navigate through MainFragment and SettingsFragment. Accepts
 * input from SettingsFragment and conveys it to MainFragment, which passes it onto a new activity.
 */

public class MyActivity extends AppCompatActivity implements SettingsFragment.OnSettingsChangedListener {

    //Access strings for retrieval of values from Intent in DisplayMessageActivity
    public final static String EXTRA_MESSAGE = "com.urgentx.blackmetal.MESSAGE";
    public final static String IMAGE_PATH = "com.urgentx.blackmetal.IMAGE";
    public final static String GREYSCALE = "com.urgentx.blackmetal.GREYSCALE";
    public final static String BLACK_FILTER = "com.urgentx.blackmetal.BLACK";
    public final static String SATURATION_FILTER = "com.urgentx.blackmetal.SATURATION";
    public final static String RED_GAMMA = "com.urgentx.blackmetal.RED";
    public final static String GREEN_GAMMA = "com.urgentx.blackmetal.GREEN";
    public final static String BLUE_GAMMA = "com.urgentx.blackmetal.BLUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);   //setup activity layout

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);  //set up layout for tabs
        tabLayout.addTab(tabLayout.newTab().setText("Take pic"));         //..
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));         //..
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);                  //..

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);   //retrieve ViewPager from layout file
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());    //initialise PagerAdapter with FragmentManager and a number of Tabs.
        viewPager.setAdapter(adapter);  //supply our PageAdapter implementation to ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());    //switch tabs
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //respond to check/uncheck of Greyscale switch in SettingsFragment
    @Override
    public void onGreyScaleSelected(boolean on) {
        //set boolean in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if (mainFragment != null) {
            mainFragment.setGreyScale(on);
        }
    }

    //respond to slide of Black filter SeekBar in SettingsFragment
    @Override
    public void onDarkSeekBarChanged(int value) {
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID"); //questionable tag

        if (mainFragment != null) {
            mainFragment.setBlackFilterValue(value);
        }
    }

    //respond to slide of Saturation filter SeekBar in SettingsFragment
    @Override
    public void onSatSeekBarChanged(int value) {
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if (mainFragment != null) {
            mainFragment.setSatFilterValue(value);
        }
    }

    //respond to slide of red gamma SeekBar in SettingsFragment
    @Override
    public void onRedSeekBarChanged(int value) {
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if (mainFragment != null) {
            mainFragment.setRedGammaValue(value);
        }
    }

    //respond to slide of green gamma SeekBar in SettingsFragment
    @Override
    public void onGreenSeekBarChanged(int value) {
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if (mainFragment != null) {
            mainFragment.setGreenGammaValue(value);
        }
    }

    //respond to slide of blue gamma SeekBar in SettingsFragment
    @Override
    public void onBlueSeekBarChanged(int value) {
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if (mainFragment != null) {
            mainFragment.setBlueGammaValue(value);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}