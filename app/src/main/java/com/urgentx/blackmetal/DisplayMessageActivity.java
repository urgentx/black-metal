package com.urgentx.blackmetal;

import android.content.ContentResolver;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

/**
 * Does all image processing using settings received in Intent from MainFragment in MainActivity.
 * Displays image and sets up Facebook API calls to share photo to Facebook using native app.
 */

public class DisplayMessageActivity extends AppCompatActivity {

    Bitmap rawImage = null;
    String bandName = null;
    String imagePath = null;
    ShareDialog shareDialog;

    //settings variables
    private boolean greyScale;
    private int blackFilterCeiling, saturationLevel, font;
    private double redGammaValue, greenGammaValue, blueGammaValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext()); //initialise Facebook SDK
        shareDialog = new ShareDialog(this);

        setContentView(R.layout.activity_display_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //set up toolbar
        toolbar.setTitle("Result");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){   //handle back button
                    onBackPressed();    //call back button behaviour
                }
            }
        });

        Snackbar.make(findViewById(R.id.activity_display_layout), "Nice!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        //retrieve image from external memory and set it to display in an ImageView
        imagePath = getIntent().getStringExtra(MyActivity.IMAGE_PATH); // retrieve path from intent

        //get settings from MainFragment in MainActivity
        greyScale = getIntent().getExtras().getBoolean(MyActivity.GREYSCALE);
        blackFilterCeiling = getIntent().getExtras().getInt(MyActivity.BLACK_FILTER);
        saturationLevel = getIntent().getExtras().getInt(MyActivity.SATURATION_FILTER);
        redGammaValue = (double) getIntent().getExtras().getInt(MyActivity.RED_GAMMA);  //cast slider int values to double
        greenGammaValue = (double) getIntent().getExtras().getInt(MyActivity.GREEN_GAMMA);   //cast slider int values to double
        blueGammaValue = (double) getIntent().getExtras().getInt(MyActivity.BLUE_GAMMA);     //cast slider int values to double
        font = getIntent().getExtras().getInt(MyActivity.FONT);

        if (imagePath != null) {
            try {
                File f= new File(imagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                rawImage = BitmapFactory.decodeStream(new FileInputStream(f), null, options).copy(Bitmap.Config.ARGB_8888, true);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
            }

            if (getWindowManager().getDefaultDisplay().getWidth() < rawImage.getWidth()) {   //check if bitmap too large for ImageView,
                int height = (rawImage.getHeight() * 512 / rawImage.getWidth());            //if so, shrink it.
                rawImage = Bitmap.createScaledBitmap(rawImage, 512, height, true);
            }

            Intent intent = getIntent();    //retrieve text entered in MyActivity
            bandName = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

            //adapt Gamma values to decimals
            redGammaValue /= 100;
            redGammaValue += 0.5;
            greenGammaValue /= 100;
            greenGammaValue += 0.5;
            blueGammaValue /= 100;
            blueGammaValue += 0.5;

            applyGamma(redGammaValue, greenGammaValue, blueGammaValue);

            ImageView imageView = (ImageView) findViewById(R.id.imgViewDisplay);   //find the imageView in our layout
            if (greyScale) {
                //old method using ImageView
                //ColorMatrix matrix = new ColorMatrix();                             //set ColorMatrix to greyscale
                //matrix.setSaturation(0);
                //ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                //imageView.setColorFilter(filter);           //apply greyscale filter to imageView

                applyGreyscale();
                applyContrastFilter(5);
            }

            applyBlackFilter(blackFilterCeiling); //distort bitmap



            if (saturationLevel > 0) { //don't saturate at all when slider progress == 0
                applySaturationFilter(saturationLevel * 2); //apply saturation
            }

            drawText(); //add text to bitmap

            imageView.setImageBitmap(rawImage);     //give ImageView our bitmap

            // galleryAddPic();

            //MediaStore.Images.Media.insertImage(getContentResolver(), rawImage, "Black Metal" , "Generated using Black Metal app"); //add picture to gallery so user can access it with system media provider
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

    // Called on click of Save to Gallery button
    public void saveToGallery(View view) {
        if(rawImage != null) {
            savePhoto(rawImage);
            Toast.makeText(getApplicationContext(), "Saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No picture taken", Toast.LENGTH_SHORT).show();
        }
    }

    private String savePhoto(Bitmap bmp) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //this is the default location inside SD Card - Pictures folder
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance(); //get Calendar to set date/time on bitmap

        String date = fromInt(c.get(Calendar.MONTH)) //build a date stamp
                + fromInt(c.get(Calendar.DAY_OF_MONTH))
                + fromInt(c.get(Calendar.YEAR))
                + fromInt(c.get(Calendar.HOUR_OF_DAY))
                + fromInt(c.get(Calendar.MINUTE))
                + fromInt(c.get(Calendar.SECOND));
        File imageFileName = new File(path, "blackmetal_" + date.toString() + ".jpg"); //imageFileFolder
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); //insert our bitmap
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFileName.toString();
    }

    private String fromInt(int val) {
        return String.valueOf(val);
    }

    //invoke system media scanner to add bitmap to system media gallery
    private void scanPhoto(String imageFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); //build intent with Uri
        File f = new File(imageFileName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent); //..and send it off
    }

    public void applyGreyscale() {
        Bitmap grayscaleBitmap = Bitmap.createBitmap(
                rawImage.getWidth(), rawImage.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(grayscaleBitmap);
        Paint p = new Paint();
        ColorMatrix cm = new ColorMatrix();

        cm.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        p.setColorFilter(filter);
        c.drawBitmap(rawImage, 0, 0, p);

        rawImage = grayscaleBitmap;
    }

    //add/subtract gamma from each image pixel
    public void applyGamma(double red, double green, double blue) {

        //get image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        // color information
        int A, R, G, B;
        int pixel;
        //constant value curve
        final int MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        //gamma arrays
        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        //set values for gamma channels
        for (int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        //apply gamma table
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                //get pixel color
                pixel = rawImage.getPixel(x, y);
                A = Color.alpha(pixel);
                //look up gamma
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                //set new color to output bitmap
                rawImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
    }

    //saturate image by converting pixels to HSV color system, increasing sat. value, then reverting back to RGB
    public void applySaturationFilter(int level) {
        //get image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        //get pixel array from source
        rawImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        //iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[1] *= level;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        //output bitmap
        rawImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        rawImage.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    public void applyContrastFilter(double value) {
        //image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        //create output bitmap

        //color information
        int A, R, G, B;
        int pixel;
        //get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        //scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                //get pixel color
                pixel = rawImage.getPixel(x, y);
                A = Color.alpha(pixel);
                //apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.red(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.red(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                //set new pixel color to output bitmap
                rawImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
    }

    //granulates the image with black dots based upon threshold of pixel color values
    public void applyBlackFilter(int blackFilterCeiling) {
        //get image size
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        int[] pixels = new int[width * height];
        //get pixel array from source
        rawImage.getPixels(pixels, 0, width, 0, 0, width, height);
        //random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 0;
        //iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(2 * blackFilterCeiling + 1);
                if (R < thresHold && G < thresHold && B < thresHold) {
                    pixels[index] = Color.rgb(0x10, 0x10, 0x10);
                }
            }
        }
        //output bitmap
        rawImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        rawImage.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    //uses a custom font to draw text on image on an arc
    public void drawText() {
        Canvas canvas = new Canvas(rawImage); //canvas for drawing on

        Path mArc;  //path to put text on
        Paint mPaintText;
        Typeface tf;
        if(font == 0){
            tf = Typeface.createFromAsset(getAssets(), "fonts/blackmetal.ttf"); //load font
        } else {
            tf = Typeface.createFromAsset(getAssets(), "fonts/pureevil.ttf"); //load font
        }

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);      //set up text properties
        mPaintText.setShadowLayer(5.0f, 10.0f, 10.0f, Color.WHITE); //set shadow
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTypeface(tf);
        mPaintText.setTextSize(170f);

        mArc = new Path();

        RectF oval = new RectF(80, 100, 400, 300); //set curve bounds
        mArc.addArc(oval, -180, 200);
        canvas.drawTextOnPath(bandName, mArc, 5, 20, mPaintText);
    }
}
