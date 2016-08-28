package com.example.asus.mptest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/8/18.
 * 按专辑分Fragment中的ListView的适配器
 */
public class AlbumListAdapter extends ArrayAdapter<String> {
    private Context mcontext;
    private ArrayList<Song> songs;
    private int resourceId;


    public AlbumListAdapter(Context context, int ViewResourceId, List<String> objects){
        super(context,ViewResourceId,objects);
        mcontext=context;
        resourceId=ViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String albumName=getItem(position);
        DatabaseHelper dbHelper=new DatabaseHelper(mcontext,"SongStore.db",null,1);
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query("recentlyAdd_list",null,"song_album=?",new String[]{albumName},null,null,null);
        int count=cursor.getCount();
        cursor.moveToFirst();
        String filePath=cursor.getString(cursor.getColumnIndex("song_path"));
        cursor.close();
        Bitmap bitmap=GetSong.createAlbumArt(filePath);

        ViewHolder viewHolder;
        View view;
        if(convertView!=null){
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }else{
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.album_art=(ImageView) view.findViewById(R.id.first_album_art);
            viewHolder.album_name=(TextView) view.findViewById(R.id.album_name);
            viewHolder.album_size=(TextView) view.findViewById(R.id.album_count);
            view.setTag(viewHolder);
        }

        if(bitmap==null){
            viewHolder.album_art.setImageResource(R.drawable.defaultart);
        }else{
            viewHolder.album_art.setImageBitmap(bitmap);
        }

        viewHolder.album_name.setText(albumName);
        viewHolder.album_size.setText(String.valueOf(count)+" 首歌曲");

        return view;
    }

    class ViewHolder{
        ImageView album_art;
        TextView album_name;
        TextView album_size;
    }
}
