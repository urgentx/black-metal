package com.urgentx.blackmetal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Barco on 27-May-16.
 */
public class SettingsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_settings_layout, container,false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        RelativeLayout linearLayout = (RelativeLayout) getActivity().findViewById(R.id.settingsfraglayout);

/*
        Button button = new Button(getContext());
        button.setText("Enter");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "CVLT!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });


        linearLayout.addView(button);*/

    }
}
