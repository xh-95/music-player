package com.example.asus.mptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ASUS on 2016/8/14.
 */
public class SongAdapter extends ArrayAdapter<Song> {

    private int resourceId;
    private int songs_Count;

    public SongAdapter(Context context, int ViewResourceId, List<Song> objects){
        super(context,ViewResourceId,objects);
        resourceId=ViewResourceId;
        songs_Count=objects.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Song song=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView!=null){
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }else{
            viewHolder=new ViewHolder();
            view=LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.song_album=(ImageView) view.findViewById(R.id.song_album_art);
            viewHolder.song_name=(TextView) view.findViewById(R.id.song_name);
            viewHolder.song_singer=(TextView) view.findViewById(R.id.song_singer);
            view.setTag(viewHolder);
        }


        //ImageView songAlbumArt=(ImageView) view.findViewById(R.id.song_album_art);
        //TextView song_name=(TextView) view.findViewById(R.id.song_name);
        //TextView song_singer=(TextView) view.findViewById(R.id.song_singer);
        viewHolder.song_name.setText(song.getFileName());
        viewHolder.song_singer.setText(song.getSinger());
        Bitmap tempmap=GetSong.createAlbumArt(song.getFileUrl());
        if(tempmap!=null){
            viewHolder.song_album.setImageBitmap(tempmap);
        }else{
            viewHolder.song_album.setImageResource(R.drawable.defaultart);
        }



        return view;
    }

    class ViewHolder{
        ImageView song_album;
        TextView song_name;
        TextView song_singer;
    }
}
