package com.example.asus.mptest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/18.
 * 按歌手划分歌曲的Fragment
 */
public class SingerFragment extends Fragment {
    private ArrayList<Song> songs;
    private ListView songlist;
    private ArrayList<String> singerNames;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.singer_fragment,container,false);

        songs=SongsSingleInstance.getSongs(getActivity());
        songlist=(ListView) view.findViewById(R.id.singer_list);

        dbHelper=new DatabaseHelper(getActivity(),"SongStore.db",null,1);
        db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select distinct song_singer from recentlyAdd_list",null);
        singerNames=new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                singerNames.add(cursor.getString(cursor.getColumnIndex("song_singer")));
            }while(cursor.moveToNext());
        }
        cursor.close();

        SingerListAdapter adapter=new SingerListAdapter(getActivity(),R.layout.singer_info,singerNames);
        songlist.setAdapter(adapter);

        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String singerName=singerNames.get(position);
                ArrayList<Song> singerSongs=new ArrayList<Song>();
                Cursor singercursor=db.query("recentlyAdd_list",null,"song_singer=?",new String[]{singerName},null,null,null);
                if(singercursor.moveToFirst()){
                    Song temp;
                    do {
                        temp=new Song();
                        temp.setFileName(singercursor.getString(singercursor.getColumnIndex("song_name")));
                        temp.setSinger(singercursor.getString(singercursor.getColumnIndex("song_singer")));
                        temp.setFileUrl(singercursor.getString(singercursor.getColumnIndex("song_path")));
                        singerSongs.add(temp);
                    }while(singercursor.moveToNext());
                }
                singercursor.close();
                ListSongsFragment.setSongs(singerSongs);
                ListSongsFragment listSongsFragment=new ListSongsFragment();
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.main_content,listSongsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
