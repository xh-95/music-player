package com.example.asus.mptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/18.
 */
public class DatabaseObserver {
    public static void DataChanged(String tableName, String itemName, SQLiteDatabase db){
        if(itemName.equals("recentlyAdd_list")){
            Cursor cursor=db.query("recentlyAdd_list",null,null,null,null,null,null);
            ContentValues values=new ContentValues();
            values.put("list_size",cursor.getCount());
            db.update("list_info",values,"list_table_name = ?", new String[]{itemName});
            cursor.close();
        }
    }
    public static void createTable(String tableName, SQLiteDatabase db){
        Cursor cursor=db.query("list_info",null,null,null,null,null,null);
        int recordCount=cursor.getCount();
        cursor.close();
        String tName="UserDefind"+String.valueOf(recordCount-4+1);
        db.execSQL("create table "+tName+" ("
                +"song_name text,"
                +"song_singer text,"
                +"song_path text primary key)");
        Log.d("DatabaseObserver","table created");
        ContentValues values=new ContentValues();
        values.put("list_table_name",tName);
        values.put("list_size",0);
        values.put("list_Chinese_name",tableName);
        db.insert("list_info",null,values);
    }

    public static ArrayList<Song> searchSong(String name, SQLiteDatabase db){
        ArrayList<Song> temp=new ArrayList<Song>();
        Cursor tcursor=db.rawQuery("select * from recentlyAdd_list where song_name like '%"+name+"%'",null);
        if(tcursor.moveToFirst()){
            Song tsong;
            do{
                tsong=new Song();
                tsong.setFileName(tcursor.getString(tcursor.getColumnIndex("song_name")));
                tsong.setSinger(tcursor.getString(tcursor.getColumnIndex("song_singer")));
                tsong.setFileUrl(tcursor.getString(tcursor.getColumnIndex("song_path")));
                temp.add(tsong);
            }while(tcursor.moveToNext());
        }
        tcursor.close();
        return temp;
    }

    public static void insertDataBySongName(String table, String [] songname, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        Cursor cursor;
        for(int i=0; i<songname.length; i++){
            cursor=db.query("recentlyAdd_list",null,"song_name = ?" , new String[]{songname[i]}, null,null,null);
            if(cursor.moveToFirst()){
                do{
                    contentValues.put("song_name",cursor.getString(cursor.getColumnIndex("song_name")));
                    contentValues.put("song_singer",cursor.getString(cursor.getColumnIndex("song_singer")));
                    contentValues.put("song_path",cursor.getString(cursor.getColumnIndex("song_path")));
                    db.insert(table,null,contentValues);
                    contentValues.clear();
                }while(cursor.moveToNext());
            }
            cursor.close();
        }


        int num=songname.length;
        Log.d("DatabaseObserver","table name: "+table);
        if(songname==null)
            Log.d("DatabaseObserver","songname is null");
        else
            Log.d("DatabaseObserver","songname: "+String.valueOf(songname.length));
        for(int i=0; i<songname.length; i++)
            Log.d("DatabaseObserver",songname[i]);
        Log.d("DatabaseObserver","data inserted");
        DatabaseObserver.upDate_listinfo(db,num,table);
    }

    public static void upDate_listinfo(SQLiteDatabase db, int num, String listTableName){
        ContentValues contentValues=new ContentValues();
        contentValues.put("list_size",num);
        db.update("list_info",contentValues,"list_table_name = ?", new String[]{listTableName});
    }

    public static void deleteList(SQLiteDatabase db, String [] listName){
        for(int i=0;i<listName.length;i++){
            db.execSQL("drop table "+listName[i]);
            db.delete("list_info", "list_table_name = ?", new String[]{listName[i]});
        }

    }


}
