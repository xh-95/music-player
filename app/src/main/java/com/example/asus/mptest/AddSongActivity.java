package com.example.asus.mptest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ASUS on 2016/8/23.
 */
public class AddSongActivity extends Activity {

    private ListView songlist;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String listTableName;
    private Button back,add;
    private String [] songNameArray;
    private String sname[];

    private ArrayList<String> songnames;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_song_layout);

        songlist=(ListView) findViewById(R.id.all_song);
        back=(Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper=new DatabaseHelper(this,"SongStore.db",null,1);
        db=dbHelper.getWritableDatabase();

        Intent intent=getIntent();
        String list_Chinese_name=intent.getStringExtra("list_Chinese_name");

        Cursor cursor=db.query("list_info", null, "list_Chinese_name = ?", new String[]{list_Chinese_name},null,null,null);
        if(cursor.moveToFirst()){
            listTableName=cursor.getString(cursor.getColumnIndex("list_table_name"));
        }

        cursor.close();



        songnames=new ArrayList<String>();
        Cursor acursor=db.query("recentlyAdd_list",new String[]{"song_name"},null,null,null,null,null);
        sname=new String[acursor.getCount()];

        if(acursor.moveToFirst()){
            do{
                songnames.add(acursor.getString(acursor.getColumnIndex("song_name")));
            }while(acursor.moveToNext());
        }

        sname=songnames.toArray(sname);
        acursor.close();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,sname);

        songlist.setAdapter(adapter);
        songlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);






        add=(Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems=songlist.getCheckedItemPositions();
                Log.d("DatabaseObserver","SparseBooleanArray:"+String.valueOf(checkedItems.size()));
                for(int i=0; i<checkedItems.size(); i++){
                    Log.d("DatabaseObserver",String.valueOf(checkedItems.keyAt(i)));
                    Log.d("DatabaseObserver",String.valueOf(checkedItems.valueAt(i)));
                }
                int num[]=new int[checkedItems.size()];
                for(int i=0; i<checkedItems.size(); i++){
                    if(checkedItems.valueAt(i)==true){
                    num[i]=checkedItems.keyAt(i);
                    Log.d("DatabaseObserver","num:"+String.valueOf(num[i]));
                    }
                }
                songNameArray=new String[num.length];
                for(int j=0; j<num.length; j++){
                    songNameArray[j]=sname[num[j]];
                    Log.d("DatabaseObserver","songName:"+songNameArray[j]);
                }


                DatabaseObserver.insertDataBySongName(listTableName,songNameArray,db);
                Intent aintent=new Intent(AddSongActivity.this,MainActivity.class);
                startActivity(aintent);
            }
        });

    }
}
