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

import java.util.List;

/**
 * Created by ASUS on 2016/8/16.
 * 音乐列表的适配器
 */
public class SongsListAdapter extends ArrayAdapter<SongsList> {
    private int resourceId;
    private Context mcontext;

    public SongsListAdapter(Context context, int ViewResourceId, List<SongsList> objects){
        super(context,ViewResourceId,objects);
        resourceId=ViewResourceId;
        mcontext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        SongsList songsList=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView!=null){
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }else{
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.list_art=(ImageView) view.findViewById(R.id.first_album_art);
            viewHolder.list_name=(TextView) view.findViewById(R.id.list_name);
            viewHolder.list_size=(TextView) view.findViewById(R.id.list_count);
            view.setTag(viewHolder);
        }

        viewHolder.list_name.setText(songsList.getListName());
        viewHolder.list_size.setText(String.valueOf(songsList.getSongsCount())+" 首歌曲");
        if(songsList.getSongsCount()==0){
            viewHolder.list_art.setImageResource(R.drawable.defaultart);
        }else{
            DatabaseHelper dbHelper=new DatabaseHelper(mcontext,"SongStore.db",null,1);
            SQLiteDatabase db=dbHelper.getReadableDatabase();
            Cursor cursor=db.query(songsList.getListTableName(),null,null,null,null,null,null);
            cursor.moveToFirst();
            String filePath=cursor.getString(cursor.getColumnIndex("song_path"));
            cursor.close();
            Bitmap bitmap=GetSong.createAlbumArt(filePath);
            if(bitmap!=null){
                viewHolder.list_art.setImageBitmap(bitmap);
            }else{
                viewHolder.list_art.setImageResource(R.drawable.defaultart);
            }

        }
        return view;
    }

    class ViewHolder{
        ImageView list_art;
        TextView list_name;
        TextView list_size;
    }
}
