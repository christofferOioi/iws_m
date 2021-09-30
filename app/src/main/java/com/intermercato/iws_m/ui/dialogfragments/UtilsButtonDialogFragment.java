package com.intermercato.iws_m.ui.dialogfragments;


import android.app.DialogFragment;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import com.intermercato.iws_m.R;

import se.oioi.intelweighblelib.Constants;

/**
 * Created by tore h on 2017-04-20.
 * holds buttons for different actions depending on the layout that is getting loaded
 */

public class UtilsButtonDialogFragment extends DialogFragment {

    private int workState;

    public static UtilsButtonDialogFragment newInstance(int workState) {

        Bundle args = new Bundle();
        args.putInt("WORKSTATE", workState);
        UtilsButtonDialogFragment fragment = new UtilsButtonDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UtilsButton", "onCreate");
        if (getArguments() != null) {
            workState = getArguments().getInt("WORKSTATE");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View getWorkStateLayout(LayoutInflater inflater, ViewGroup parent, int workstate) {
        View rootView = null;
        Log.d("buttonfragment", "workmode "+workstate);
        boolean isContainer = false;
        switch (workstate) {

            case Constants.WorkStates.FULL:
                rootView = inflater.inflate(R.layout.fragment_scale_result_automatic,parent, false);
                break;
            case Constants.WorkStates.SEMI:
                rootView = inflater.inflate(R.layout.fragment_scale_result_semiautomatic,parent, false);
                break;

            case Constants.WorkStates.NORMAL:
                Log.d("WORK", "NORMAL");
                if (isContainer) {
                    rootView = inflater.inflate(R.layout.fragment_scale_result_container_normal,parent, false);
                } else {
                    Log.d("WORK", "NORMAL NO CONTAINER");
                    rootView = inflater.inflate(R.layout.fragment_scale_result_normal,parent, false);
                }
                break;

            case Constants.WorkStates.STATIC: {
                      rootView = inflater.inflate(R.layout.fragment_scale_result_static,parent, false);
                }
                break;

            default:

                break;
        }
        return rootView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        }
        return getWorkStateLayout(inflater,container, workState);
    }


    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.utils_button_dialog_fragment_width);
        int height = getResources().getDimensionPixelSize(R.dimen.utils_button_dialog_fragment_height);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }

}
