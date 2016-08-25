package com.example.asus.mptest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/16.
 */
public class GetList {
    public static ArrayList<SongsList> getAllList(Context context, SQLiteDatabase db){
        ArrayList<SongsList> songsList=new ArrayList<SongsList>();
        Cursor cursor=db.query("list_info",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            SongsList songsList1;
            do{
                songsList1=new SongsList();
                songsList1.setListTableName(cursor.getString(cursor.getColumnIndex("list_table_name")));
                songsList1.setListName(cursor.getString(cursor.getColumnIndex("list_Chinese_name")));
                songsList1.setSongsCount(cursor.getInt(cursor.getColumnIndex("list_size")));
                songsList.add(songsList1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return songsList;
    }
}
