package com.example.asus.mptest;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/12.
 * 测试使用
 */
public class TestActivity extends Activity {

    private TextView songsCount;


    @Override
    protected void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.test2);

        songsCount=(TextView) findViewById(R.id.text_view);
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DURATION},
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        cursor.moveToFirst();
        StringBuilder stringBuilder=new StringBuilder();
        do{
            stringBuilder.append(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))+"/"+String.valueOf(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))+"\n");
        }while(cursor.moveToNext());
        cursor.close();
        songsCount.setText(stringBuilder);
    }
}
