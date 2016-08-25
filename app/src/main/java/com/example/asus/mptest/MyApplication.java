package com.example.asus.mptest;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 2016/8/22.
 */
public class MyApplication extends Application {
    private static Context context;
    private static PlayService.PlayBinder playBinder;
    private ServiceConnection connection;

    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();

        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                playBinder=(PlayService.PlayBinder) service;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        Intent bindIntent=new Intent(this,PlayService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }

    public static Context getContext(){
        return context;
    }

    public static PlayService.PlayBinder getBinder(){
        return playBinder;
    }


}
