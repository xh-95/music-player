package com.example.asus.mptest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/18.
 * 显示所有音乐的Fragment
 */
public class SongsFragment extends Fragment {

    private ArrayList<Song> songs;
    private ListView songlist;
    private TextView songsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.test_layout,container,false);

        songs=SongsSingleInstance.getSongs(getActivity());
        songlist=(ListView) view.findViewById(R.id.song_list);

        View view1= LayoutInflater.from(getActivity()).inflate(R.layout.header_layout,null);
        songsCount=(TextView) view1.findViewById(R.id.songscount);
        songsCount.setText("共 "+songs.size()+" 首歌曲");

        SongAdapter songAdapter=new SongAdapter(getActivity(), R.layout.song_info, songs);
        songlist.addHeaderView(view1);
        songlist.setAdapter(songAdapter);

        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song clickSong=songs.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("song_name",clickSong.getFileName());
                bundle.putString("song_singer",clickSong.getSinger());
                bundle.putString("song_path",clickSong.getFileUrl());
                bundle.putInt("request_code",1);
                Intent intent=new Intent(getActivity(),PlayActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }
}
