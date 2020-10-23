package com.young.ws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SecondActivity extends AppCompatActivity {

    MovieListFragment movieListFragment;
    MapsFragment mapsFragment;
    SettingFragment settingFragment;

    ActionBar actionBar;
    SharedPreferences sp;

    public boolean notice;
    public boolean vibe;
    public int vibeValue;

    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sp = getSharedPreferences(Contants.SETTING, MODE_PRIVATE);
        notice = sp.getBoolean(Contants.SETTING_NOTICE,true);
        vibe = sp.getBoolean(Contants.SETTING_VIBE, true);
        vibeValue = sp.getInt(Contants.SETTING_VIBE_VALUE,5);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setLogo(R.drawable.ic_movie);
        actionBar.setTitle("Movie");

        movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapsFragment = new MapsFragment();
        settingFragment = new SettingFragment();

        FirebaseMessaging.getInstance().subscribeToTopic("car").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "FCM Complete ...";
                if(!task.isSuccessful()){
                    msg = "FCM Fail";
                }
                Log.d(Contants.LOG_TAG,msg);
            }
        });

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(receiver, new IntentFilter(Contants.NOTIFICATION));

}

    public void ckBtn(View v){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(v.getId()==R.id.listBtn){
            movieListFragment = new MovieListFragment();
            transaction.replace(
                    R.id.fragment, movieListFragment
            ).commit();
        }else if(v.getId()==R.id.mapBtn){
            mapsFragment = new MapsFragment();
            transaction.replace(
                    R.id.fragment, mapsFragment
            ).commit();
        }else if(v.getId() == R.id.setBtn){
            settingFragment = new SettingFragment();
            transaction.replace(
                    R.id.fragment, settingFragment
            ).commit();
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            notice = sp.getBoolean(Contants.SETTING_NOTICE,true);

            if(intent!=null && notice){
                String title = intent.getStringExtra(Contants.NOTIFICATION_TITLE);
                String body = intent.getStringExtra(Contants.NOTIFICATION_BODY);
                String control = intent.getStringExtra(Contants.NOTIFICATION_CONTROL);
                String data = intent.getStringExtra(Contants.NOTIFICATION_DATA);
                Log.d(Contants.LOG_TAG, title+", "+body);

                vibe = sp.getBoolean(Contants.SETTING_VIBE, true);
                if(vibe){
                    doVibrator();
                }
//                doRing();
                doNoti(title, body);
            }
        }
    };
    public void doVibrator(){
        vibeValue = sp.getInt(Contants.SETTING_VIBE_VALUE,5);

        Log.d(Contants.LOG_TAG, "Seekbar"+vibeValue);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000,vibeValue*25));
        }else {
            vibrator.vibrate(1000);
        }
    }
    public void doRing(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(),uri);
        ringtone.play();
    }
    public void doNoti(String title, String content){
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT>=26){
            if(manager.getNotificationChannel("ch2")==null){
                manager.createNotificationChannel(new NotificationChannel("ch2","chname", NotificationManager.IMPORTANCE_DEFAULT));
            }
            builder = new NotificationCompat.Builder(this, "ch2");

        }else{
            builder = new NotificationCompat.Builder(this);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_snow);

        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification noti = builder.build();
        manager.notify(2,noti);
    }


}