package com.young.ws;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {

    SharedPreferences sp;
    LinearLayout linearLayout;

    Switch notiSwitch;
    Switch vibeSwitch;
    SeekBar vibeSeek;
    TextView vibeValue;
    TextView appInfo;

    public SettingFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        linearLayout = (LinearLayout) viewGroup.findViewById(R.id.setlayout);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        notiSwitch = viewGroup.findViewById(R.id.notiSwitch);
        vibeSwitch = viewGroup.findViewById(R.id.vibeSwitch);
        vibeSeek = viewGroup.findViewById(R.id.seekBar);
        vibeValue = viewGroup.findViewById(R.id.vibeText);

        sp = getActivity().getSharedPreferences(Contants.SETTING, MODE_PRIVATE);
        boolean noti = sp.getBoolean(Contants.SETTING_NOTICE,true);
        boolean vibe = sp.getBoolean(Contants.SETTING_VIBE,true);
        notiSwitch.setChecked(noti);
        if(!noti){
            vibeSwitch.setClickable(false);
            vibeSwitch.setEnabled(false);
            vibeSeek.setEnabled(false);
        }else{
            vibeSwitch.setChecked(vibe);
            vibeSwitch.setEnabled(true);
            if(!vibe){
                vibeSeek.setEnabled(false);
            }else {
                vibeSeek.setEnabled(true);
            }
        }
        notiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(Contants.SETTING_NOTICE, b);
                edit.commit();
                if(b){
                    boolean vi = sp.getBoolean(Contants.SETTING_VIBE,true);
                    vibeSwitch.setChecked(vi);
                    vibeSwitch.setEnabled(true);
                    if(vi)
                        vibeSeek.setEnabled(true);
                }else{
                    vibeSwitch.setEnabled(false);
//                    vibeSwitch.setChecked(false);
                    vibeSeek.setEnabled(false);
                }
            }
        });
        vibeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(Contants.SETTING_VIBE, b);
                edit.commit();
                if(b){
                    vibeSeek.setEnabled(true);
                }else{
                    vibeSeek.setEnabled(false);
                }
            }
        });
        int v = sp.getInt(Contants.SETTING_VIBE_VALUE, 5);
        vibeValue.setText(v+"");
        vibeSeek.setProgress(v);
        vibeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                vibeValue.setText(i+"");
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(Contants.SETTING_VIBE_VALUE, i);
                edit.commit();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        appInfo = (viewGroup).findViewById(R.id.appinfo);
        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View alertView = inflater.inflate(R.layout.info, null);
                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());

                builder.setView(alertView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        return viewGroup;
    }
}