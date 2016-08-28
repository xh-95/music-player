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
import android.widget.Toast;

/**
 * Created by ASUS on 2016/8/24.
 * 删除播放列表的Activity
 */
public class DeleteListActivity extends Activity {

    private ListView listList;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Button back,delete_back;
    private String [] list_table_name,list_Chinese_name,listTableArray;

    /**
     *回调函数，初始化Activity，显示可删除列表，即用户所创建播放列表
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_list_layout);

        listList=(ListView) findViewById(R.id.all_list);
        back=(Button) findViewById(R.id.delete_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper=new DatabaseHelper(this,"SongStore.db",null,1);
        db=dbHelper.getWritableDatabase();

        //Intent intent=getIntent();

        Cursor cursor=db.query("list_info",null,null,null,null,null,null);
        list_table_name=new String[cursor.getCount()-3];
        list_Chinese_name=new String[cursor.getCount()-3];
        cursor.moveToFirst();
        int i=0;
        do{
            String listName=cursor.getString(cursor.getColumnIndex("list_table_name"));
            if(!listName.equals("recentlyAdd_list") && !listName.equals("recentlyPlay_list") && !listName.equals("frequentlyPlay_list") && !listName.equals("like_list")){
                list_table_name[i]=cursor.getString(cursor.getColumnIndex("list_table_name"));
                list_Chinese_name[i]=cursor.getString(cursor.getColumnIndex("list_Chinese_name"));
                i++;
            }
        }while(cursor.moveToNext());
        cursor.close();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list_Chinese_name);
        listList.setAdapter(adapter);
        listList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        delete_back=(Button) findViewById(R.id.delete);
        delete_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems=listList.getCheckedItemPositions();
                int num[]=new int[checkedItems.size()];
                for(int i=0; i<checkedItems.size(); i++){
                    if(checkedItems.valueAt(i)==true){
                        num[i]=checkedItems.keyAt(i);
                    }
                }
                listTableArray=new String[num.length];
                for(int j=0; j<num.length; j++){
                    listTableArray[j]=list_table_name[num[j]];
                }

                DatabaseObserver.deleteList(db,listTableArray);
                finish();
            }
        });

    }
}
