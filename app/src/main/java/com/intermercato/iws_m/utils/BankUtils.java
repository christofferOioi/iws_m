package com.intermercato.iws_m.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.intermercato.iws_m.Constants;
import com.intermercato.iws_m.mApplication;

import static com.intermercato.iws_m.Constants.DISPLAYED_BANKS;
import static com.intermercato.iws_m.Constants.OIOI_PREFS;


public class BankUtils {


    public static int getBanksToDisplay(Context context) {

        return context.getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE).getInt(Constants.DISPLAYED_BANKS, 4);
    }


    public static void setBanksToDisplay(int numberOfBanks){

        SharedPreferences.Editor editor = mApplication.Companion.applicationContext().getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(DISPLAYED_BANKS, numberOfBanks);
        editor.commit();
    }

    public static int getBanksWidth(int numberOfBanks, int width) {
        int gridLayoutMargin = 0;
        int banks = numberOfBanks;//getBanksToDisplay(BleStandardApplication.getInstance());

        int calculatedWidth;
        if (banks <= 3) {
            //Log.d("active","<= 3 "+banks);
            calculatedWidth = ((width - gridLayoutMargin) /banks);
        } else {
            //Log.d("active","> 3 "+banks);
            calculatedWidth = ((width - gridLayoutMargin) / 3);
        }

        return calculatedWidth;
    }

}
