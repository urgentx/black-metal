package com.urgentx.blackmetal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    //called on click of Back to App button
    public void goBack(View view) {
        finish();
    }
}
