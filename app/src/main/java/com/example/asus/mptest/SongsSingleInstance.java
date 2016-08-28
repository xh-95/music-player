package com.example.asus.mptest;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/15.
 * 单例类，获取本地媒体库中所有的音乐
 */
public class SongsSingleInstance {
    private static ArrayList<Song> songs=null;
    public static ArrayList<Song> getSongs(Context context){
        if(songs==null){
            songs=GetSong.getAllSongs(context);
            return songs;
        }else{
            return songs;
        }
    }
}
