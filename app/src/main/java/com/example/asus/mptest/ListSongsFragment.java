package com.example.asus.mptest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/19.
 * 所有音乐Fragment
 */
public class ListSongsFragment extends Fragment {

    private static ArrayList<Song> targetSongs;
    private ListView listView;
    private static String listTableName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.test_layout,container,false);
        listView=(ListView) view.findViewById(R.id.song_list);

        SongAdapter songAdapter=new SongAdapter(getActivity(), R.layout.song_info, targetSongs);
        listView.setAdapter(songAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song clickSong=targetSongs.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("song_name",clickSong.getFileName());
                bundle.putString("song_singer",clickSong.getSinger());
                bundle.putString("song_path",clickSong.getFileUrl());
                bundle.putString("list_table_name",listTableName);
                bundle.putInt("request_code",1);
                Intent intent=new Intent(getActivity(),PlayActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        return view;
    }

    public static void setSongs(ArrayList<Song> objects){
        targetSongs=objects;
        listTableName="recentlyAdd_list";
    }
    public static void setSongs(ArrayList<Song> objects, String lTableName){
        targetSongs=objects;
        if(lTableName==null || lTableName.equals("")){
            listTableName="recentlyAdd_list";
        }
        listTableName=lTableName;
    }

    public static String getTableName(){


        return listTableName;
    }

}
