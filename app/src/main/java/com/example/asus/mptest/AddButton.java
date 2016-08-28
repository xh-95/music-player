package com.example.asus.mptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by ASUS on 2016/8/17.
 * 自定义控件，用作创建新的播放列表
 */
public class AddButton extends LinearLayout{
    private Context mcontext;

    public AddButton(Context context){
        super(context);
        mcontext=context;
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.add_button, this);
        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);

    }



}
