package com.urgentx.blackmetal;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by Barco on 27-May-16.
 */
public class SettingsFragment extends Fragment{

    OnSettingsChangedListener mCallback;

    //Container Activity MUST implement this interface for communication from this fragment
    public interface OnSettingsChangedListener{
        public void onGreyScaleSelected(boolean on);
        public void onSatSeekBarChanged(int value);
        public void onDarkSeekBarChanged(int value);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //make sure that container Activity implements callback interface. If not, throw exception.
        try{
            mCallback = (OnSettingsChangedListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnSettingsChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_settings_layout, container,false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.settingsfraglayout);

        //Greyscale switch
        SwitchCompat s = (SwitchCompat) getView().findViewById(R.id.switch1);
        s.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on){
                mCallback.onGreyScaleSelected(on);
            }
        });

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
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), "Black filter changed",
                        Toast.LENGTH_SHORT).show();
            }
        });

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
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), "Saturation filter changed",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
