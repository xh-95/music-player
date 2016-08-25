package com.example.asus.mptest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ASUS on 2016/8/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static final String CREATE_LIST_INFO="create table list_info("
            +"list_table_name text primary key,"
            +"list_size integer,"
            +"list_Chinese_name text)";
    private static final String CREATE_RECENTLYADD_LIST="create table recentlyAdd_list ("
            +"song_name text,"
            +"song_singer text,"
            +"song_path text primary key,"
            +"song_album integer)";
    /*private static final String CREATE_RECENTLYPLAY_LIST="create table recentlyPlay_list ("
            +"song_name text,"
            +"song_singer text,"
            +"song_path text primary key,"
            +"song_play_time integer)";*/
    private static final String CREATE_FREQUENTLYPLAY_LIST="create table frequentlyPlay_list ("
            +"song_name text,"
            +"song_singer text,"
            +"song_path text primary key,"
            +"song_play_count integer)";
    private static final String CREATE_LIKE_LIST="create table like_list ("
            +"song_name text,"
            +"song_singer text,"
            +"song_path text primary key)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_LIST_INFO);
        db.execSQL(CREATE_RECENTLYADD_LIST);
        //db.execSQL(CREATE_RECENTLYPLAY_LIST);
        db.execSQL(CREATE_FREQUENTLYPLAY_LIST);
        db.execSQL(CREATE_LIKE_LIST);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
