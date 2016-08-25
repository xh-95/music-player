package com.example.asus.mptest;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 2016/8/21.
 */
public class PlayService extends Service {

    public static final int SET_DATA = 0;
    public static final int START = 1;
    public static final int PAUSE = 2;
    public static final int RESET = 3;
    public static final int SEEKTO = 4;


    private PlayBinder pBinder=new PlayBinder();
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Message message;
    private String currentSongPath;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    class PlayBinder extends Binder{
        public void setData(String filePath){
            currentSongPath=filePath;
            Bundle bundle=new Bundle();
            bundle.putString("song_path",filePath);
            message=new Message();
            message.what=SET_DATA;
            message.setData(bundle);
            handler.sendMessage(message);

        }
        public void startPlay(){
            message=new Message();
            message.what=START;
            handler.sendMessage(message);

            dbHelper=new DatabaseHelper(MyApplication.getContext(),"SongStore.db",null,1);
            db=dbHelper.getWritableDatabase();
            Cursor cursor=db.query("frequentlyPlay_list", null, "song_path = ?", new String[]{currentSongPath}, null,null,null);

            ContentValues values=new ContentValues();
            cursor.moveToFirst();
            if(cursor.getCount()==0){
                Cursor tcursor=db.query("recentlyAdd_list", null, "song_path = ?", new String[]{currentSongPath}, null,null,null);
                tcursor.moveToFirst();
                values.put("song_name",tcursor.getString(tcursor.getColumnIndex("song_name")));
                values.put("song_singer",tcursor.getString(tcursor.getColumnIndex("song_singer")));
                values.put("song_path",tcursor.getString(tcursor.getColumnIndex("song_path")));
                values.put("song_play_count",1);
                db.insert("frequentlyPlay_list",null,values);
                tcursor.close();
            }else{
                int oldCount=cursor.getInt(cursor.getColumnIndex("song_play_count"));
                oldCount++;
                values.put("song_play_count", oldCount);
                db.update("frequentlyPlay_list", values, "song_path = ?", new String[]{currentSongPath});
            }
            cursor.close();

            Cursor pcursor=db.query("frequentlyPlay_list",null,null,null,null,null,null);
            int songCount=pcursor.getCount();
            DatabaseObserver.upDate_listinfo(db, songCount, "frequentlyPlay_list");
            pcursor.close();

        }
        public void pausePlay(){
            message=new Message();
            message.what=PAUSE;
            handler.sendMessage(message);
        }
        public void resetPlay(){
            message=new Message();
            message.what=RESET;
            handler.sendMessage(message);
        }
        public boolean getIsPlaying(){
            if(mediaPlayer.isPlaying())
                return true;
            else
                return false;
        }
        public int getCurrentPosition(){
            return mediaPlayer.getCurrentPosition();
        }
        public void playerSeekTo(int position){
            Bundle bundle=new Bundle();
            bundle.putInt("currPosition",position);
            message=new Message();
            message.what=SEEKTO;
            message.setData(bundle);
            handler.sendMessage(message);
        }
        public String getCurrPath(){
            return currentSongPath;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        new Thread(new mediaPlay()).start();

    }

    @Override
    public IBinder onBind(Intent intent){
        return pBinder;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    class mediaPlay implements Runnable{
        @Override
        public void run() {
            Looper.prepare();
            handler=new Handler(){
                public void handleMessage(Message message){
                    switch (message.what){
                        case SET_DATA:{

                            Bundle bundle=message.getData();
                            try{
                                mediaPlayer.setDataSource(bundle.getString("song_path"));
                                mediaPlayer.prepare();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            break;
                        }
                        case START:{
                            mediaPlayer.start();

                            break;
                        }
                        case PAUSE:{
                            mediaPlayer.pause();
                            break;
                        }
                        case RESET:{
                            mediaPlayer.reset();
                            stopSelf();
                            break;
                        }
                        case SEEKTO:{
                            Bundle bundle=message.getData();
                            int position=bundle.getInt("currPosition");
                            mediaPlayer.seekTo(position);
                            break;
                        }
                        default:
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }
}
