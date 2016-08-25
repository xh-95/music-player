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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/18.
 */
public class ListFragment extends Fragment {

    private ListView songlist;
    private ArrayList<SongsList> songsLists;
    private View temp;
    private ViewGroup vg;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.list_fragment,container,false);
        temp=view;
        vg=container;
        super.onResume();
        LayoutInflater.from(getActivity()).inflate(R.layout.list_fragment,vg,false);

        dbHelper=new DatabaseHelper(getActivity(),"SongStore.db",null,1);
        db=dbHelper.getWritableDatabase();

        songlist=(ListView) temp.findViewById(R.id.songs_list);
        songsLists=GetList.getAllList(getActivity(),db);

        View view1=LayoutInflater.from(getActivity()).inflate(R.layout.header_layout,null);

        SongsListAdapter songsListAdapter=new SongsListAdapter(getActivity(),R.layout.list_info,songsLists);
        songlist.setAdapter(songsListAdapter);
        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongsList songList=songsLists.get(position);
                String tableName=songList.getListTableName();
                Cursor cursor1=db.query(tableName,null,null,null,null,null,null);
                if(cursor1.getCount()==0){
                    cursor1.close();
                    Toast.makeText(getActivity(),"此列表中尚未添加歌曲",Toast.LENGTH_SHORT).show();
                    //return;
                }
                ArrayList<Song> songArrayList=new ArrayList<Song>();
                if(cursor1.moveToFirst()){
                    Song temp;
                    do{
                        temp=new Song();
                        temp.setFileName(cursor1.getString(cursor1.getColumnIndex("song_name")));
                        temp.setSinger(cursor1.getString(cursor1.getColumnIndex("song_singer")));
                        temp.setFileUrl(cursor1.getString(cursor1.getColumnIndex("song_path")));
                        songArrayList.add(temp);
                    }while(cursor1.moveToNext());
                }
                cursor1.close();
                ListSongsFragment.setSongs(songArrayList,tableName);
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
