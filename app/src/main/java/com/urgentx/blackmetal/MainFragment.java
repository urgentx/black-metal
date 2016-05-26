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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;

import java.io.File;

/**
 * Created by Barco on 25-May-16.
 */
public class MainFragment extends Fragment {

    EditText editText;
    public final static String EXTRA_MESSAGE = "com.urgentx.blackmetal.MESSAGE";
    public final static String IMAGE_PATH = "com.urgentx.blackmetal.IMAGE";
    String imagePath; //path to user-taken image

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_main_layout, container,false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        RelativeLayout linearLayout = (RelativeLayout) getActivity().findViewById(R.id.mainfraglayout);


        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fragfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Snap a fab pic!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                takePhoto();
            }
        });

        editText = (EditText) getView().findViewById(R.id.main_fragment_edittext);


        Button button = (Button) getView().findViewById(R.id.main_fragment_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });




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
        startActivity(intent);
    }
}
