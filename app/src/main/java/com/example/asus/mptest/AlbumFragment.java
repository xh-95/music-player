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
 */
public class AlbumFragment extends Fragment {

    private ArrayList<Song> songs;
    private ArrayList<String> albumNames;
    private ListView songlist;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.album_fragment,container,false);

        songs=SongsSingleInstance.getSongs(getActivity());
        songlist=(ListView) view.findViewById(R.id.album_list);

        dbHelper=new DatabaseHelper(getActivity(),"SongStore.db",null,1);
        db=dbHelper.getReadableDatabase();
        final Cursor cursor=db.rawQuery("select distinct song_album from recentlyAdd_list",null);
        albumNames=new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                albumNames.add(cursor.getString(cursor.getColumnIndex("song_album")));
            }while(cursor.moveToNext());
        }
        cursor.close();

        AlbumListAdapter adapter=new AlbumListAdapter(getActivity(),R.layout.album_info,albumNames);
        songlist.setAdapter(adapter);

        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String albumName=albumNames.get(position);
                ArrayList<Song> albumSongs=new ArrayList<Song>();
                Cursor albumcursor=db.query("recentlyAdd_list",null,"song_album=?",new String[]{albumName},null,null,null);
                if(albumcursor.moveToFirst()){
                    Song temp;
                    do {
                        temp=new Song();
                        temp.setFileName(albumcursor.getString(albumcursor.getColumnIndex("song_name")));
                        temp.setSinger(albumcursor.getString(albumcursor.getColumnIndex("song_singer")));
                        temp.setFileUrl(albumcursor.getString(albumcursor.getColumnIndex("song_path")));
                        albumSongs.add(temp);
                    }while(albumcursor.moveToNext());
                }
                albumcursor.close();
                ListSongsFragment.setSongs(albumSongs);
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
