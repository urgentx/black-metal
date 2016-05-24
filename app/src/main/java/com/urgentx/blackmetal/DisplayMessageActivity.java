package com.urgentx.blackmetal;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;

public class DisplayMessageActivity extends AppCompatActivity {

    Bitmap rawImage = null;
    String bandName = null;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        setContentView(R.layout.activity_display_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Result");
        setSupportActionBar(toolbar);

        //retrieve image from external memory and set it to display in an ImageView
        String imagePath = getIntent().getStringExtra(MyActivity.IMAGE_PATH); // retrieve path from intent

        if (imagePath != null) {
            try {
                ContentResolver cr = getContentResolver();  //user ContentResolver to access image
                rawImage = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, Uri.parse(imagePath));  //decode into bitmap
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
            }

            if(getWindowManager().getDefaultDisplay().getWidth() <  rawImage.getWidth()){   //check if bitmap too large for ImageView,
                int height = (rawImage.getHeight() * 512 / rawImage.getWidth());            //if so, shrink it.
                rawImage = Bitmap.createScaledBitmap(rawImage, 512, height, true);
            }

            ColorMatrix matrix = new ColorMatrix();                             //set ColorMatrix to greyscale
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

            applyBlackFilter(); //distort bitmap
            createContrast(130);
            Intent intent = getIntent();    //retrieve text entered in MyActivity
            bandName = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

            drawText(); //add text to bitmap


            ImageView imageView = (ImageView) findViewById(R.id.imgViewDisplay);   //find the imageView in our layout
            imageView.setColorFilter(filter);           //apply greyscale filter to imageView
            imageView.setImageBitmap(rawImage);     //give ImageView our bitmap

        }

    }

    // Called on click of Share button
    public void shareContent(View view) {
        Bitmap image = rawImage;
        SharePhoto photo = new SharePhoto.Builder() //convert image to SharePhoto
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder() //build PhotoContent from photo
                .addPhoto(photo)
                .build();

        shareDialog.show(content);  //show user share Dialog
    }

    public void createContrast( double value) {
        // image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        // create output bitmap

        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = rawImage.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                rawImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
    }

    public void applyBlackFilter() {
        // get image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        rawImage.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(0xBF);
                if(R < thresHold && G < thresHold && B < thresHold) {
                    pixels[index] = Color.rgb(0x10, 0x10, 0x10);
                }
            }
        }
        // output bitmap
        rawImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        rawImage.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    public void drawText() {
        Canvas canvas = new Canvas(rawImage); //canvas for drawing on

        Path mArc;  //path to put text on
        Paint mPaintText;

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/blackmetal.ttf"); //load font
        Typeface tf2 = Typeface.create(tf, Typeface.BOLD);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);      //set up text properties
        mPaintText.setShadowLayer(5.0f,10.0f, 10.0f, Color.WHITE); //set shadow
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTypeface(tf2);
        mPaintText.setTextSize(170f);


        mArc = new Path();

        RectF oval = new RectF(80,100,400,300); //set curve bounds
        mArc.addArc(oval, -180, 200);
        canvas.drawTextOnPath(bandName, mArc, 5, 20, mPaintText);

       /* boolean full = false;
        String[] splitArray = bandName.split("\\s+");
        Log.d("lol", "jjajaja " + splitArray.length);
        for(int x = 0; x < splitArray.length; x++){
            Log.d("hexa", "_" + splitArray[x]);
        }
        int i = 1, j = 0;
        String tempString = new String();
        while (!full){
            if(j >= splitArray.length){
                full = true;
                break;
            }
            RectF oval = new RectF(80, 100+(150*(i-1)), 400, 300+(150*(i-1)));
            mArc = new Path();
            mArc.addArc(oval, -180, 200);
            if (tempString.concat(splitArray[j]).length() < i * 10){
                tempString = tempString.concat(splitArray[j]);
                j++;
            } else{
                canvas.drawTextOnPath(tempString, mArc, 0, 20, mPaintText);
                tempString = null;
                i++;
            }
        }
        */



    }

}
