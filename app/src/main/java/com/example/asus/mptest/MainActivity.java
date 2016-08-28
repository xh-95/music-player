package com.example.asus.mptest;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 主Activity，包含顶端功能条，中部Fragment，底部简化控制布局
 */
public class MainActivity extends Activity {

    private PopupMenu popup;

    private LinearLayout mLinearLayout,nLinearLayout;
    //private ListView songlist;
    private ListButton listButton;
    private AddButton addButton;
    private Button search_button,delete_button,panel_previous,panel_playOrpause,next,panel_album_art;
    private TextView panel_song_name,panel_song_singer;
    private String currPath=null,list_name;
    private  Cursor panelCursor;
    private ArrayList<String> pathArrayList, nameArrayList, songerArrayList;
    private int currentSongIndex;


    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private PlayService.PlayBinder playBinder;

    //private ArrayList<SongsList> songsLists;
    /**
     *回调函数，获取SQLite操作对象，调用数据库初始化函数，设置顶部功能区各控件的响应事件
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        dbHelper=new DatabaseHelper(this,"SongStore.db",null,1);
        db=dbHelper.getWritableDatabase();

        final Cursor cursor=db.query("list_info",null,null,null,null,null,null);
        if(cursor.getCount()==0){
            Initialization(this,db);
        }
        cursor.close();


        search_button=(Button) findViewById(R.id.search);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout createTable=(LinearLayout) LayoutInflater.from (MainActivity.this).inflate(R.layout.search_dialog,null);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.search_dialog_title)
                        .setView(createTable)
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText=(EditText) createTable.findViewById(R.id.search_dialog_input);
                                //DatabaseObserver.createTable(editText.getText().toString(),db);
                                ArrayList<Song> temp=new ArrayList<Song>();
                                temp=DatabaseObserver.searchSong(editText.getText().toString(),db);
                                ListSongsFragment.setSongs(temp);
                                ListSongsFragment searchSongsFragment=new ListSongsFragment();
                                FragmentManager fragmentManager=getFragmentManager();
                                FragmentTransaction transaction=fragmentManager.beginTransaction();
                                transaction.replace(R.id.main_content,searchSongsFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();

            }
        });
        delete_button=(Button) findViewById(R.id.delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=db.query("list_info",null,null,null,null,null,null);
                if(cursor.getCount()>3){
                    Intent intent=new Intent(MainActivity.this,DeleteListActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,"无可删除列表",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //TextView check=(TextView) findViewById(R.id.isEmpty);

        listButton=new ListButton(this);
        mLinearLayout=(LinearLayout) findViewById(R.id.box);
        mLinearLayout.addView(listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupButtonClick(listButton);
            }
        });

        addButton=new AddButton(this);
        nLinearLayout=(LinearLayout) findViewById(R.id.add_button_layout);
        nLinearLayout.addView(addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout createTable=(LinearLayout) LayoutInflater.from (MainActivity.this).inflate(R.layout.create_list_dialog,null);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.DialogTitle)
                        .setView(createTable)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText=(EditText) createTable.findViewById(R.id.dialog_input);
                                DatabaseObserver.createTable(editText.getText().toString(),db);
                                Intent intent=new Intent(MainActivity.this,AddSongActivity.class);
                                intent.putExtra("list_Chinese_name",editText.getText().toString());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        });



    }

    /**
     *回调函数，设置主Activity底部控制布局中各控件的事件响应
     */
    @Override
    protected void onResume(){
        super.onResume();

        playBinder=MyApplication.getBinder();
        panel_previous=(Button) findViewById(R.id.panel_previous);
        panel_playOrpause=(Button) findViewById(R.id.panel_playOrpause);
        next=(Button) findViewById(R.id.next);
        panel_album_art=(Button) findViewById(R.id.panel_album_art);
        panel_song_name=(TextView) findViewById(R.id.panel_song_name);
        panel_song_singer=(TextView) findViewById(R.id.panel_song_singer);

        if(playBinder!=null && playBinder.getIsPlaying()){
            panel_playOrpause.setBackgroundResource(R.drawable.pause);
        }else if(playBinder!=null && !playBinder.getIsPlaying()){
            panel_playOrpause.setBackgroundResource(R.drawable.play);
        }
        panel_playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playBinder.getIsPlaying()){
                    playBinder.pausePlay();

                    panel_playOrpause.setBackgroundResource(R.drawable.play);
                }else{
                    playBinder.startPlay();

                    panel_playOrpause.setBackgroundResource(R.drawable.pause);
                }
            }
        });

        list_name=PlayActivity.getListName();
        if(list_name!=null){
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

            Cursor lcursor=db.query("recentlyAdd_list", new String[]{"song_name","song_singer"}, "song_path = ?", new String[]{playBinder.getCurrPath()},null,null,null);
            lcursor.moveToFirst();
            currentSongIndex=nameArrayList.indexOf(lcursor.getString(0));
            lcursor.close();

            panel_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentSongIndex-1<0){
                        Toast.makeText(MainActivity.this,"已经是第一首歌曲",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    panel_song_name.setText(nameArrayList.get(currentSongIndex-1));
                    panel_song_singer.setText(songerArrayList.get(currentSongIndex-1));
                    panel_album_art.setBackground(new BitmapDrawable(GetSong.createAlbumArt(pathArrayList.get(currentSongIndex-1))));
                    playBinder.resetPlay();
                    playBinder.setData(pathArrayList.get(currentSongIndex-1));
                    playBinder.startPlay();

                    currentSongIndex--;
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((currentSongIndex+1)>(pathArrayList.size()-1)){
                        Toast.makeText(MainActivity.this,"已经是最后一首歌曲",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    panel_song_name.setText(nameArrayList.get(currentSongIndex+1));
                    panel_song_singer.setText(songerArrayList.get(currentSongIndex+1));
                    Bitmap tbitmap=GetSong.createAlbumArt(pathArrayList.get(currentSongIndex+1));
                    if(tbitmap!=null){
                        panel_album_art.setBackground(new BitmapDrawable(tbitmap));
                    }else{
                        panel_album_art.setBackgroundResource(R.drawable.defaultart);
                    }
                    playBinder.resetPlay();
                    playBinder.setData(pathArrayList.get(currentSongIndex+1));
                    playBinder.startPlay();
                    currentSongIndex++;
                }
            });
        }


        if(playBinder==null){
            panel_album_art.setBackgroundResource(R.drawable.defaultart);
            panel_song_name.setText("未知歌曲");
            panel_song_singer.setText("未知歌手");
        }else{
            currPath=playBinder.getCurrPath();
            if(currPath==null){
                panel_album_art.setBackgroundResource(R.drawable.defaultart);
            }else{
                Drawable drawable=new BitmapDrawable(GetSong.createAlbumArt(currPath));
                panel_album_art.setBackground(drawable);

                panelCursor=db.query("recentlyAdd_list", new String[]{"song_name","song_singer"}, "song_path = ?", new String[]{currPath},null,null,null);
                panelCursor.moveToFirst();
                panel_song_name.setText(panelCursor.getString(0));
                panel_song_singer.setText(panelCursor.getString(1));

                panel_album_art.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle=new Bundle();
                        bundle.putString("song_name",panelCursor.getString(0));
                        bundle.putString("song_singer",panelCursor.getString(1));
                        bundle.putString("song_path",currPath);
                        bundle.putString("list_table_name",ListSongsFragment.getTableName());
                        bundle.putInt("currPosition",playBinder.getCurrentPosition());
                        bundle.putInt("request_code",0);
                        panelCursor.close();
                        Intent intent=new Intent(MainActivity.this,PlayActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     *实现弹出菜单，点击选项切换相应Fragment
     */
    public void onPopupButtonClick(View button){
        popup=new PopupMenu(this,button);
        getMenuInflater().inflate(R.menu.first_popup_menu,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                TextView textView=(TextView) findViewById(R.id.playing_list);
                textView.setText(item.getTitle());
                FragmentManager fragmentManager=getFragmentManager();
                switch(item.getItemId()){
                    case R.id.list:

                        ListFragment listFragment=new ListFragment();
                        FragmentTransaction listTransaction=fragmentManager.beginTransaction();
                        listTransaction.replace(R.id.main_content,listFragment);
                        listTransaction.commit();

                        break;
                    case R.id.song:

                        SongsFragment songfragment=new SongsFragment();
                        FragmentTransaction songTransaction=fragmentManager.beginTransaction();
                        songTransaction.replace(R.id.main_content,songfragment);
                        songTransaction.commit();
                        break;
                    case R.id.album:

                        AlbumFragment albumFragment=new AlbumFragment();
                        FragmentTransaction albumTransaction=fragmentManager.beginTransaction();
                        albumTransaction.replace(R.id.main_content,albumFragment);
                        albumTransaction.commit();
                        break;
                    case R.id.singer:

                        SingerFragment singerFragment=new SingerFragment();
                        FragmentTransaction singerTransaction=fragmentManager.beginTransaction();
                        singerTransaction.replace(R.id.main_content,singerFragment);
                        singerTransaction.commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     *数据库初始化函数，应用安装时初始化默认播放列表数据
     */
    public void Initialization(Context context,SQLiteDatabase db){
        ContentValues values=new ContentValues();
        values.put("list_table_name","recentlyAdd_list");
        values.put("list_size",0);
        values.put("list_Chinese_name","最近添加");
        db.insert("list_info",null,values);
        values.clear();
        values.put("list_table_name","frequentlyPlay_list");
        values.put("list_size",0);
        values.put("list_Chinese_name","最常播放");
        db.insert("list_info",null,values);
        values.clear();
        values.put("list_table_name","like_list");
        values.put("list_size",0);
        values.put("list_Chinese_name","收藏曲目");
        db.insert("list_info",null,values);
        values.clear();

        ArrayList<Song> arrList=GetSong.getAllSongs(this);
        Iterator<Song> it=arrList.iterator();
        while(it.hasNext()){
            Song song=it.next();
            values.put("song_name",song.getFileName());
            values.put("song_singer",song.getSinger());
            values.put("song_path",song.getFileUrl());
            values.put("song_album",song.getAlbum());
            db.insert("recentlyAdd_list",null,values);
            values.clear();
        }
        DatabaseObserver.DataChanged("list_info","recentlyAdd_list",db);
    }
}
