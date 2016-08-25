package com.example.asus.mptest;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 2016/8/20.
 */
public class PlayActivity extends Activity {

    private ImageView albumView;
    private LinearLayout root_layout;
    private Button backup,playOrpause,addToLike,previous,next;
    private TextView play_state,playing_song_name,playing_singer_name,current_time,total_time;
    private PlayService.PlayBinder playBinder=null;
    private SeekBar seekBar;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    private boolean isLike;
    private static String list_name;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int currentSongIndex;
    private ArrayList<String> pathArrayList, nameArrayList, songerArrayList;

    private ServiceConnection connection;

    private Bundle bundle;
    private Handler handler;
    private static final int CHANGE_CURRENT_TIME=0;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.playing_layout);

        Intent intent=getIntent();
        bundle=intent.getExtras();


        playBinder=MyApplication.getBinder();





        backup=(Button) findViewById(R.id.backup);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        current_time=(TextView) findViewById(R.id.current_time);
        total_time=(TextView) findViewById(R.id.total_time);
        root_layout=(LinearLayout) findViewById(R.id.playing_layout);
        albumView=(ImageView) findViewById(R.id.album_pic);
        play_state=(TextView) findViewById(R.id.play_state);
        playing_song_name=(TextView) findViewById(R.id.playing_song_name);
        playing_singer_name=(TextView) findViewById(R.id.playing_singer_name);
        playOrpause=(Button) findViewById(R.id.playOrpause);
        playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playBinder.getIsPlaying()){
                    playBinder.pausePlay();
                    play_state.setText(R.string.play_state_pause);
                    playOrpause.setBackgroundResource(R.drawable.play);
                }else{
                    playBinder.startPlay();
                    play_state.setText(R.string.play_state_playing);
                    playOrpause.setBackgroundResource(R.drawable.pause);
                }
            }
        });
        addToLike=(Button) findViewById(R.id.addToLike);
        isLike=false;
        dbHelper=new DatabaseHelper(this,"SongStore.db",null,1);
        db=dbHelper.getWritableDatabase();
        Cursor addcursor=db.query("like_list",null,"song_name = ?",new String[]{bundle.getString("song_name")},null,null,null);
        if(addcursor.getCount()==0){
            addToLike.setBackgroundResource(R.drawable.like);
            isLike=false;
        }else{
            addToLike.setBackgroundResource(R.drawable.liked);
            isLike=true;
        }
        addcursor.close();
        addToLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLike==false){
                    addToLike.setBackgroundResource(R.drawable.liked);
                    ContentValues values=new ContentValues();
                    values.put("song_name",bundle.getString("song_name"));
                    values.put("song_singer",bundle.getString("song_singer"));
                    values.put("song_path",bundle.getString("song_path"));
                    db.insert("like_list",null,values);
                    values.clear();
                    Cursor qcursor=db.query("like_list",null,null,null,null,null,null);
                    int count=qcursor.getCount();
                    qcursor.close();
                    DatabaseObserver.upDate_listinfo(db,count,"like_list");
                }else{
                    addToLike.setBackgroundResource(R.drawable.like);
                    db.delete("like_list","song_name = ?",new String[]{bundle.getString("song_name")});
                    Cursor pcursor=db.query("like_list",null,null,null,null,null,null);
                    int count1=pcursor.getCount();
                    DatabaseObserver.upDate_listinfo(db,count1,"like_list");
                }
            }
        });

        list_name=bundle.getString("list_table_name");
        Cursor listCursor=db.query(list_name,new String[]{"song_path","song_name","song_singer"},null,null,null,null,null);
        pathArrayList=new ArrayList<String>();
        nameArrayList=new ArrayList<String>();
        songerArrayList=new ArrayList<String>();
        if(listCursor.moveToFirst()){
            do{
                pathArrayList.add(listCursor.getString(0));
                nameArrayList.add(listCursor.getString(1));
                songerArrayList.add(listCursor.getString(2));
            }while(listCursor.moveToNext());
        }
        listCursor.close();
        currentSongIndex=pathArrayList.indexOf(bundle.getString("song_path"));
        previous=(Button) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSongIndex-1<0){
                    Toast.makeText(PlayActivity.this,"已经是第一首歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                setLayout(1,pathArrayList.get(currentSongIndex-1),nameArrayList.get(currentSongIndex-1),songerArrayList.get(currentSongIndex-1));
                playBinder.resetPlay();
                playBinder.setData(pathArrayList.get(currentSongIndex-1));
                playBinder.startPlay();
                currentSongIndex--;
            }
        });
        next=(Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((currentSongIndex+1)>(pathArrayList.size()-1)){
                    Toast.makeText(PlayActivity.this,"已经是最后一首歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                setLayout(1,pathArrayList.get(currentSongIndex+1),nameArrayList.get(currentSongIndex+1),songerArrayList.get(currentSongIndex+1));
                playBinder.resetPlay();
                playBinder.setData(pathArrayList.get(currentSongIndex+1));
                playBinder.startPlay();
                currentSongIndex++;
            }
        });




        setLayout(bundle.getInt("request_code"),bundle.getString("song_path"),bundle.getString("song_name"),bundle.getString("song_singer"));


        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Media.DURATION},
                MediaStore.Audio.Media.DATA + "=?",
                new String[] {bundle.getString("song_path")}, null);
        cursor.moveToFirst();
        int totalTime=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        cursor.close();
        int mi=(totalTime / 1000) / 60;
        int si=(totalTime / 1000) % 60;
        String ms,ss;
        if(mi < 10){
            ms="0"+String.valueOf(mi);
        }else{
            ms=String.valueOf(mi);
        }
        if(si < 10){
            ss="0"+String.valueOf(si);
        }else{
            ss=String.valueOf(si);
        }
        total_time.setText(ms+":"+ss);

        seekBar=(SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(totalTime / 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playBinder.playerSeekTo(seekBar.getProgress() * 1000);
                isChanging=false;
            }
        });

        handler=new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case CHANGE_CURRENT_TIME:
                    {
                        Bundle getCurrBundle=message.getData();
                        int time=getCurrBundle.getInt("current_time");
                        String ctime;
                        if((time % 60)<10)
                            ctime="0"+(time / 60)+":0"+(time % 60);
                        else
                            ctime="0"+(time / 60)+":"+(time % 60);
                        current_time.setText(ctime);
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(isChanging==true) {
                    return;
                }
                if(playBinder.getCurrentPosition()!=0){
                    int sProgress=playBinder.getCurrentPosition() / 1000;
                    seekBar.setProgress(sProgress);
                    Bundle currBundle=new Bundle();
                    currBundle.putInt("current_time",sProgress);
                    Message message=new Message();
                    message.what=CHANGE_CURRENT_TIME;
                    message.setData(currBundle);
                    handler.sendMessage(message);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);




    }

    public static String getListName(){
        return list_name;
    }

    public void setLayout(int requestCode, String songPath, String song_name, String song_singer){
        Bitmap abitmap,bbitmap,albumBitmap;;
        albumBitmap=GetSong.createAlbumArt(songPath);
        if(albumBitmap==null){
            abitmap=makeRoundCorner(BitmapFactory.decodeResource(getResources(),R.drawable.defaultart));
            bbitmap=BlurUtil.blurBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.defaultart),PlayActivity.this);
        }else{
            abitmap=makeRoundCorner(albumBitmap);
            bbitmap=BlurUtil.blurBitmap(albumBitmap, PlayActivity.this);
        }
        albumView.setImageBitmap(abitmap);
        root_layout.setBackground(new BitmapDrawable(bbitmap));

        playing_song_name.setText(song_name);
        playing_singer_name.setText(song_singer);
        if(requestCode==1){
            playBinder.resetPlay();
            playBinder.setData(bundle.getString("song_path"));
            playBinder.startPlay();
            play_state.setText(R.string.play_state_playing);
            playOrpause.setBackgroundResource(R.drawable.pause);

        }else if(requestCode==0){
            if(playBinder.getIsPlaying()){
                //int currPos=bundle.getInt("currPosition");
                play_state.setText(R.string.play_state_playing);
                playOrpause.setBackgroundResource(R.drawable.pause);
                //playBinder.playerSeekTo(currPos);
            }else{

                play_state.setText(R.string.play_state_pause);
                playOrpause.setBackgroundResource(R.drawable.play);

            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //unbindService(connection);
    }

    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }


        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
