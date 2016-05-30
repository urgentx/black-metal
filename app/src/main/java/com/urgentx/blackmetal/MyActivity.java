package com.urgentx.blackmetal;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyActivity extends AppCompatActivity implements SettingsFragment.OnSettingsChangedListener {

    //Access strings
    public final static String EXTRA_MESSAGE = "com.urgentx.blackmetal.MESSAGE";
    public final static String IMAGE_PATH = "com.urgentx.blackmetal.IMAGE";
    public final static String GREYSCALE = "com.urgentx.blackmetal.GREYSCALE";
    public final static String BLACK_FILTER = "com.urgentx.blackmetal.BLACK";
    public final static String SATURATION_FILTER = "com.urgentx.blackmetal.SATURATION";
    String imagePath; //path to user-taken image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);  //set up layout for tabs
        tabLayout.addTab(tabLayout.newTab().setText("Take pic"));         //..
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));         //..
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);                  //..

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
 /*
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();

      //Initialise Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Snap a fab pic!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                takePhoto();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Called on click of Send button
    /*public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        if (imagePath != null) {
            intent.putExtra(IMAGE_PATH, imagePath);  //include path to stored bmp
        }
        startActivity(intent);
    }

    private static final int TAKE_PICTURE = 1;  //request code
    private Uri imageUri;

    //create new intent and request that it dumps photo in our file
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //make camera intent
        File photo = new File(Environment.getExternalStorageDirectory(), "Black_metal_pic.jpg"); //create a file in external storage
        intent.putExtra(MediaStore.EXTRA_OUTPUT,        //request extra output
                Uri.fromFile(photo));                   //..to our URI
        imageUri = Uri.fromFile(photo);                 //save our URI for accessing image later
        startActivityForResult(intent, TAKE_PICTURE);   //start activity with request identifier so we can catch the result
    }

    //catch result of camera activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //overhead method, always called
        switch (requestCode) {
            case TAKE_PICTURE:                                 //check for match with our request code
                if (resultCode == Activity.RESULT_OK) {        //pic successful
                    Uri selectedImage = imageUri;              //load our URI
                    getContentResolver().notifyChange(selectedImage, null);     //notify ContentResolver of new image @ URI
                    Toast.makeText(this, "File saved @ " + selectedImage.toString(),
                            Toast.LENGTH_LONG).show();
                    imagePath = selectedImage.toString();      //set our imagePath to our URI

                }

        }*/

    }

    //respond to check/uncheck of Greyscale switch in SettingsFragment
    @Override
    public void onGreyScaleSelected(boolean on) {
        //set boolean in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if(mainFragment != null) {
            mainFragment.setGreyScale(on);
        }
    }

    //respond to slide of Black filter SeekBar in SettingsFragment
    @Override
    public void onDarkSeekBarChanged(int value){
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if(mainFragment != null){
            mainFragment.setBlackFilterValue(value);
        }
    }

    //respond to slide of Saturation filter SeekBar in SettingsFragment
    @Override
    public void onSatSeekBarChanged(int value){
        //set int in MainFragment
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragID");

        if(mainFragment != null){
            mainFragment.setSatFilterValue(value);
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