package com.intermercato.iws_m.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intermercato.iws_m.R;
import com.intermercato.iws_m.mApplication;
import com.intermercato.iws_m.repositories.PrefsRepo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


import se.oioi.intelweighblelib.Constants;


/**
 * Created by christoffer hedin on 2019-06-12.
 */
public final class WeightUtils {

    private static final double LB_FACTOR = 2.20462262D;
    private static final double KG_FACTOR = 0.45359237D;
    private static final double SHORT_TONS_FACTOR = 0.0011023;
    private static final String TAG = "weightutils";

    /**
     * @param value double that is formatted
     * @return double that has 1 decimal place
     */

    public static String formatWeight(boolean showSumSign, int value) {

        String massUnitString = PrefsRepo.getWeightUnit(); //pref.getString("setMassUnitKey", "0");
        Context context = mApplication.Companion.applicationContext();
        String weightsign = context.getString(R.string.txt_sum);

        if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_KG)) {
            // Log.d(TAG,"kg --->");
            if (showSumSign) {
                return String.format("%s %d", weightsign, value);
            } else {
                return String.format("%d ", value);
            }
        } else if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_LB)) {
            //Log.d(TAG,"lb --->");
            double newval = convertKgToLb(value);

            DecimalFormat df = new DecimalFormat("###.#", new DecimalFormatSymbols(Locale.US));

            if (showSumSign) {
                return String.format("%s %s", weightsign, df.format(newval));
            } else {
                return String.format("%s ", df.format(newval));
            }
        } else if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_SHORT_TONS)) {
            // Log.d(TAG,"short tons --->");
            DecimalFormat df = new DecimalFormat("###.####", new DecimalFormatSymbols(Locale.US));
            double dval = convertKgToShortTons(value);

            if (showSumSign) {
                return String.format("%s %.1f", weightsign, dval);
            } else {

                return String.format("%.1f", dval);
            }

        } else {
            Log.d(TAG, "nada: ");
            return "";
        }
    }

    public static String formatWeightWithMassUnit(boolean showSumSign, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mApplication.Companion.applicationContext());
        final String massUnitString = pref.getString("setMassUnitKey", "kg");
        Context context = mApplication.Companion.applicationContext();
        Log.d(TAG, "massUnitString " + massUnitString);
        String weightsign = context.getString(R.string.txt_sum);


        if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_KG)) {

            if (showSumSign) {
                Log.d(TAG, "massUnitString " + weightsign+" "+value+"  "+massUnitString);
                return String.format("%s %d %s", weightsign, value, massUnitString);
            } else {
                return String.format("%s %s", value, massUnitString);
            }


        } else if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_LB)) {
            double newval = convertKgToLb(value);
            DecimalFormat df = new DecimalFormat("###.#", new DecimalFormatSymbols(Locale.US));

            if (showSumSign) {
                return String.format("%s %s %s", weightsign, df.format(newval), massUnitString);
            } else {
                return String.format("%s %s", df.format(newval), massUnitString);
            }

        } else if (massUnitString.equalsIgnoreCase(Constants.MassUnits.UNIT_SHORT_TONS)) {

            DecimalFormat df = new DecimalFormat("###.####", new DecimalFormatSymbols(Locale.US));

            double dval = convertKgToShortTons(value);

            if (showSumSign) {
                return String.format("%s %.1f %s", weightsign, dval, massUnitString);
            } else {

                return String.format("%.1f %s", dval, massUnitString);
            }

        } else {
            return "";
        }

    }

    public static double convertKgToShortTons(double kg) {
        return (kg * SHORT_TONS_FACTOR);
    }

    public static int convertKgToLb(double kg) {
        return (int) (kg * LB_FACTOR);
    }

    public static double convertLbToKg(double lb) {
        return (int) (lb * KG_FACTOR);
    }

}
