package com.urgentx.blackmetal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

/**
 * Main app logic goes on here. This fragment displays an EditText with app description/instructions,
 * as well as using native API to let the user take a picture. Accepts information about settings
 * from MainActivity, and then sends all picture/text information in an Intent to DisplayMessageActivity
 */

public class MainFragment extends Fragment {
    //values for our settings to be updated from MainActivity intent
    private boolean greyScale;
    private int blackFilterValue;
    private int satFilterValue;

    private int redGammaValue;
    private int greenGammaValue;
    private int blueGammaValue;

    EditText editText;

    //access strings
    public final static String EXTRA_MESSAGE = "com.urgentx.blackmetal.MESSAGE";
    public final static String IMAGE_PATH = "com.urgentx.blackmetal.IMAGE";
    public final static String GREYSCALE = "com.urgentx.blackmetal.GREYSCALE";
    public final static String BLACK_FILTER = "com.urgentx.blackmetal.BLACK";
    public final static String SATURATION_FILTER = "com.urgentx.blackmetal.SATURATION";
    public final static String RED_GAMMA = "com.urgentx.blackmetal.RED";
    public final static String GREEN_GAMMA = "com.urgentx.blackmetal.GREEN";
    public final static String BLUE_GAMMA = "com.urgentx.blackmetal.BLUE";

    String imagePath; //path to user-taken image

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //inflate our layout container
        return inflater.inflate(R.layout.fragment_main_layout, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.mainfraglayout); //in case we want to alter layout

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fragfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Snap a pic!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                takePhoto();
            }
        });

        editText = (EditText) getView().findViewById(R.id.main_fragment_edittext);

        Button button = (Button) getView().findViewById(R.id.main_fragment_button); //set up button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

        //settings defaults
        greyScale = true; //set pic to be greyscale by default
        blackFilterValue = 75;  //default blackFilter value
        satFilterValue = 50;    //default satFilter value

    }

    //create new intent and request that it dumps photo in our file
    private static final int TAKE_PICTURE = 1;  //request code
    private Uri imageUri;

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
                    getActivity().getContentResolver().notifyChange(selectedImage, null);     //notify ContentResolver of new image @ URI
                    Toast.makeText(getContext(), "File saved @ " + selectedImage.toString(),
                            Toast.LENGTH_LONG).show();
                    imagePath = selectedImage.toString();      //set our imagePath to our URI

                }

        }

    }

    //Called on click of Send button
    public void sendMessage() {

        Snackbar.make(getView().getRootView(), "CVLT!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);

        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        if (imagePath != null) {
            intent.putExtra(IMAGE_PATH, imagePath);  //include path to stored bmp
        }
        //add settings to intent
        intent.putExtra(GREYSCALE, greyScale);
        intent.putExtra(BLACK_FILTER, blackFilterValue);
        intent.putExtra(SATURATION_FILTER, satFilterValue);
        intent.putExtra(RED_GAMMA, redGammaValue);
        intent.putExtra(GREEN_GAMMA, greenGammaValue);
        intent.putExtra(BLUE_GAMMA, blueGammaValue);
        startActivity(intent);
    }

    //getters/setters

    public boolean isGreyScale() {
        return greyScale;
    }

    public void setGreyScale(boolean greyScale) {
        this.greyScale = greyScale;
    }

    public int getBlackFilterValue() {
        return blackFilterValue;
    }

    public void setBlackFilterValue(int blackFilterValue) {
        this.blackFilterValue = blackFilterValue;
    }

    public int getSatFilterValue() {
        return satFilterValue;
    }

    public void setSatFilterValue(int satFilterValue) {
        this.satFilterValue = satFilterValue;
    }


    public int getRedGammaValue() {
        return redGammaValue;
    }

    public void setRedGammaValue(int redGammaValue) {
        this.redGammaValue = redGammaValue;
    }

    public int getGreenGammaValue() {
        return greenGammaValue;
    }

    public void setGreenGammaValue(int greenGammaValue) {
        this.greenGammaValue = greenGammaValue;
    }

    public int getBlueGammaValue() {
        return blueGammaValue;
    }

    public void setBlueGammaValue(int blueGammaValue) {
        this.blueGammaValue = blueGammaValue;
    }

}
