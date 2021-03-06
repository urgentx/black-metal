package com.urgentx.blackmetal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private int font;

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
    public final static String FONT = "com.urgentx.blackmetal.FONT";

    public enum PicType {        //keep track of camera or from-file mode for src pic
        FromFile, FromCamera
    }

    String imagePath = null; //path to user-taken image
    String selectedImagePath = null; //path to user-selected image

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //inflate our layout container
        return inflater.inflate(R.layout.fragment_main_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FloatingActionButton fab = getView().findViewById(R.id.fragfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Snap a pic!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    askForPermission();
                } else {
                    dispatchTakePictureIntent();
                }

            }
        });


        Button button = (Button) getView().findViewById(R.id.file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                } else {
                    dispatchSelectPictureIntent();
                }
            }
        });

        editText = (EditText) getView().findViewById(R.id.main_fragment_edittext);

        Button button1 = (Button) getView().findViewById(R.id.main_fragment_button); //set up button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        //settings defaults
        greyScale = true; //set pic to be greyscale by default
        blackFilterValue = 75;  //default blackFilter value
        satFilterValue = 50;    //default satFilter value
        font = 0;
    }

    //create new intent and request that it dumps photo in our file
    private static final int CAMERA_REQUEST = 1;  //request code
    private static final int EXT_STORAGE_REQUEST = 2; //request code
    private static final int CAMERA_PERMISSION = 3;
    private static final int STORAGE_PERMISSION = 4;
    private Uri imageUri = null;    //Uri for snapshot image

    private void askForPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.urgentx.blackmetal.fileprovider",
                        photoFile);
                imageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void dispatchSelectPictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, EXT_STORAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getActivity(), "This app needs permission to use your camera to take photos.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchSelectPictureIntent();
            } else {
                Toast.makeText(getActivity(), "This app needs permission to access your storage.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //catch result of camera activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //overhead method, always called
        switch (requestCode) {
            case EXT_STORAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        imageUri = data.getData();
                        loadImagePreview(imageUri);
                        selectedImagePath = imageUri.toString();
                    }
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    loadImagePreview(imageUri);
                } else {
                    imageUri = null; //Forget about image storage location
                }
                break;
        }
    }

    private void loadImagePreview(Uri uri) {
        try {
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
            Bitmap rawImage = BitmapFactory.decodeStream(imageStream).copy(Bitmap.Config.ARGB_8888, true);
            ((ImageView) getActivity().findViewById(R.id.preview_pic)).setImageBitmap(rawImage);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
        }
    }

    //Called on click of Send button
    public void sendMessage() {
        if (imageUri != null) {
            Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            intent.putExtra(IMAGE_PATH, imageUri);  //include path to stored bmp
            //add settings to intent
            intent.putExtra(GREYSCALE, greyScale);
            intent.putExtra(BLACK_FILTER, blackFilterValue);
            intent.putExtra(SATURATION_FILTER, satFilterValue);
            intent.putExtra(RED_GAMMA, redGammaValue);
            intent.putExtra(GREEN_GAMMA, greenGammaValue);
            intent.putExtra(BLUE_GAMMA, blueGammaValue);
            intent.putExtra(FONT, font);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Please provide a photo.", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = image.getAbsolutePath();
        return image;
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

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }
}
