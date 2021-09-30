package com.intermercato.iws_m.repositories;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intermercato.iws_m.Constants;
import com.intermercato.iws_m.R;
import com.intermercato.iws_m.mApplication;

import java.util.Locale;
import java.util.Map;

import static com.intermercato.iws_m.Constants.CONTROLBOX_SETUP;
import static com.intermercato.iws_m.Constants.DRIVER_ID;
import static com.intermercato.iws_m.Constants.OIOI_PREFS;
import static com.intermercato.iws_m.Constants.SCALE_ID;


/**
 * Created by fredrik_ortman on 2014-01-27.
 */
public class PrefsRepo {


    public static int getContainerMode(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return Integer.parseInt(pref.getString("setContainerMode", "0"));
    }

    public static String getTripTotalWeightMeterTextSize(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return pref.getString("tripTotalWeightMeterTextSize", "24");
    }

    public static boolean isGpsEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("gpsKey", false);
    }

    public static boolean isTripTotalMeter(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("tripTotalWeightMeter", false);
    }

    public static boolean isOrientationInLandscape(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return sharedPreferences.getBoolean("setOrientationPortraitKey", false);
    }


    public static int getNbrOfCopies() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return Integer.valueOf(pref.getString("setNumberOfCopiesKey", "1"));
    }

    public static boolean isWeightInKg() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        String[] strArray = mApplication.Companion.applicationContext().getResources().getStringArray(R.array.settings_setup_operationMode_masUnit_array);
        String weightUnit = pref.getString("setMassUnitKey", strArray[0]);

        return weightUnit.equals("kg");
    }

    public static String createNewOrderNumber(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        int lastId = prefs.getInt("lastOrderId",0);
        String prefix = prefs.getString("setPrefixSessionNumberKey","ORD");
        int newId = ++lastId;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("lastOrderId", newId);
        editor.apply();

        Log.d("CREATEORDERNUMBER", "" + newId);

        return prefix + String.format(Locale.US,"%06d", newId);
    }

    public static void clearLastOrderNumber() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("lastOrderId");
        editor.apply();
    }

    /**
     * GET FORCED LOGIN - Using SharedPreferences
     *
     *
     * @return weightUnit
     */

    public static boolean getForcedLogin(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return  pref.getBoolean("forceLoginKey",false);
    }

    /**
     *
     */

    public static boolean getShowActiveOrderTotal(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        return  pref.getBoolean("sumActiveOrdersKey",false);
    }

    /**
     * Set Mass Weight Unit - Using SharedPreferences
     *
     *
     * @return weightUnit
     */
    public static String getWeightUnit(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        ///String [] strArray = BleStandardApplication.getContext().getResources().getStringArray(R.array.settings_setup_operationMode_masUnit_array);
        String [] strArray = mApplication.Companion.applicationContext().getResources().getStringArray(R.array.settings_setup_operationMode_masUnit_values);
        return pref.getString("setMassUnitKey",strArray[0]);
    }

    /**
     * SET CURRENT DRIVER - Using SharedPreferences
     *
     * @param  driverID long
     */

    public static void setCurrentDriver(long driverID){

        SharedPreferences.Editor editor = mApplication.Companion.applicationContext().getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(DRIVER_ID, driverID);
        editor.commit();
    }

    public static void setScaleId(String scaleId){

        SharedPreferences.Editor editor = mApplication.Companion.applicationContext().getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SCALE_ID,scaleId);
        editor.commit();

    }

    public static void setControlBoxSetup(String type){
        SharedPreferences.Editor editor = mApplication.Companion.applicationContext().getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(CONTROLBOX_SETUP,type);
        editor.commit();

    }

    public static String getControlBoxSetup(){
        SharedPreferences pref = mApplication.Companion.applicationContext().getSharedPreferences(OIOI_PREFS, Context.MODE_PRIVATE);
        return  pref.getString(CONTROLBOX_SETUP,null);

    }

    public static String getScaleId(){
        return mApplication.Companion.applicationContext().getSharedPreferences(Constants.OIOI_PREFS, Context.MODE_PRIVATE).getString("scaleId", "");
    }

    public static void listDefaultPrefs(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> keys = prefs.getAll();
        try {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d("map default values", entry.getKey() + ": " + entry.getValue().toString());
            }
        } catch (Exception e) {
                Log.d("Exception","e "+e);
        }
    }

    public static void listAllOioiPrefs(Context context){

        SharedPreferences oioiPrefs = context.getSharedPreferences(Constants.OIOI_PREFS, Context.MODE_PRIVATE);

        Map<String, ?> keys = oioiPrefs.getAll();
        try {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d("map oiOiPrefs values", entry.getKey() + ": " +entry.hashCode()+" "+ entry.getValue().toString());
            }
        } catch (Exception e) {
            Log.d("Exception","e "+e);
        }
    }
}
