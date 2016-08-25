package com.example.asus.mptest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by ASUS on 2016/8/9.
 */
public class ListButton extends LinearLayout {

    public ListButton(Context context){
        super(context);
        //LayoutInflater li=(LayoutInflater) ((Activity)context).getLayoutInflater();
        //li.inflate(R.layout.list_button,this);
        //上下两段效果一样，也可用from(Context context)来获取LayoutInflater
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_button, this);

        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);
        //setPadding(15,15,15,15);

    }

}
