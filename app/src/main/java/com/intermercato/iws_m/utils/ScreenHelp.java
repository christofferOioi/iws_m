package com.intermercato.iws_m.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenHelp {

    public static int[] getScreenWidthAndHeight(Activity act){
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return new int[]{width,height};
    }
}
