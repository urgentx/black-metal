package com.urgentx.blackmetal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Fragment with a number of  sliders and switches  with listeners to accept user input. Binds
 * communication with MainActivity using interface OnSettingsChangedListener.
 */

public class SettingsFragment extends Fragment {

    OnSettingsChangedListener mCallback;

    //Container Activity MUST implement this interface for communication from this fragment
    public interface OnSettingsChangedListener {
        public void onGreyScaleSelected(boolean on);

        public void onSatSeekBarChanged(int value);

        public void onDarkSeekBarChanged(int value);

        public void onRedSeekBarChanged(int value);

        public void onGreenSeekBarChanged(int value);

        public void onBlueSeekBarChanged(int value);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //make sure that container Activity implements callback interface. If not, throw exception.
        try {
            mCallback = (OnSettingsChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSettingsChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.settingsfraglayout); //in case we need to add anything to layout

        //set up Greyscale switch listener
        SwitchCompat s = (SwitchCompat) getView().findViewById(R.id.switch1);
        s.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                mCallback.onGreyScaleSelected(on);
                Toast.makeText(getContext(), "Black/white mode!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //set up Dark Filter SeekBar listener
        SeekBar darkSeekBar = (SeekBar) getView().findViewById(R.id.blackSeekBar);
        darkSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  //set listener for darkSeekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCallback.onDarkSeekBarChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  //notify user of successful change?
               /* Toast.makeText(getContext(), "Black filter changed",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        //set up Saturation Filter SeekBar listener
        SeekBar satSeekBar = (SeekBar) getView().findViewById(R.id.satSeekBar);
        satSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {   //set listener for satSeekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCallback.onSatSeekBarChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  //notify user of successful change?
                /*Toast.makeText(getContext(), "Saturation filter changed",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        //set up Red Gamma SeekBar listener
        SeekBar redSeekBar = (SeekBar) getView().findViewById(R.id.redSeekBar);
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {   //set listener for satSeekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCallback.onRedSeekBarChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  //notify user of successful change?
                /*Toast.makeText(getContext(), "Red gamma changed",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        //set up Green Gamma SeekBar listener
        SeekBar greenSeekBar = (SeekBar) getView().findViewById(R.id.greenSeekBar);
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {   //set listener for satSeekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCallback.onGreenSeekBarChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  //notify user of successful change?
                /*Toast.makeText(getContext(), "Green gamma changed",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        //set up Blue Gamma SeekBar listener
        SeekBar blueSeekBar = (SeekBar) getView().findViewById(R.id.blueSeekBar);
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {   //set listener for satSeekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCallback.onBlueSeekBarChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  //notify user of successful change?
                /*Toast.makeText(getContext(), "Blue gamma changed",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }
}
