package com.example.xuqi.qqdemo.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by xuqi on 17/3/23.
 */

public class UtilTools {

    //设置字体
    public static void setFont(Context mContext, TextView textView){
        // TTF为字体文件，在assets文件夹下
        Typeface fontType = Typeface.createFromAsset(mContext.getAssets(),"fonts/HWXK.ttf");
        textView.setTypeface(fontType);
    }
}
