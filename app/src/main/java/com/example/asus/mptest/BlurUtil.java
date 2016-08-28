package com.example.asus.mptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Created by ASUS on 2016/8/20.
 * 高斯模糊实现类
 */
public class BlurUtil {
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        blurScript.setRadius(25.f);

        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        allOut.copyTo(outBitmap);


        rs.destroy();

        return outBitmap;
    }

    public static Bitmap getBlurBitmap(View rootView, Context context) {
        try {
            if (rootView == null || context == null) {
                return null;
            }
            rootView.setDrawingCacheEnabled(true);
            Bitmap drawingCache = rootView.getDrawingCache();
            Bitmap bgBitmap = Bitmap.createBitmap(drawingCache);
            return BlurUtil.blurBitmap(bgBitmap, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
