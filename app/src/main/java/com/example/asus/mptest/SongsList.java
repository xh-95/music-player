package com.example.asus.mptest;

/**
 * Created by ASUS on 2016/8/16.
 * 描述播放列表的类
 */
public class SongsList {
    private String listName;
    private String listTableName;
    private int songsCount;

    public SongsList(){
        listName=null;
        listTableName=null;
        songsCount=0;
    }

    public SongsList(String listName,String listTableName, int songsCount){
        this.listName=listName;
        this.listTableName=listTableName;
        this.songsCount=songsCount;
    }

    public void setListName(String listName){
        this.listName=listName;
    }
    public void setListTableName(String listTableName){
        this.listTableName=listTableName;
    }
    public void setSongsCount(int songsCount){
        this.songsCount=songsCount;
    }
    public String getListName(){
        return listName;
    }
    public String getListTableName(){
        return listTableName;
    }
    public int getSongsCount(){
        return songsCount;
    }
}
