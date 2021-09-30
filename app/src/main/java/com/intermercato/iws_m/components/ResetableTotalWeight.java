package com.intermercato.iws_m.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.intermercato.iws_m.R;
import com.intermercato.iws_m.repositories.PrefsRepo;
import com.intermercato.iws_m.utils.WeightUtils;


/**
 * Created by c 2020 02 11
 */
public class ResetableTotalWeight extends ConstraintLayout implements View.OnClickListener {


    private ImageView tripMeterImage;
    private Button txtResettableTotalWeight;
    private Context context;
    private View v;
    private Integer colorChoice;
    private Integer textSize;
    private final String TAG = "reset";
    private ResettableCallBack listener;
    private boolean resettable = true;
    private boolean showAlertDialog = true;

    public interface ResettableCallBack {
        void onResetTotalWeight();
    }

    public ResetableTotalWeight(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setResettableCallBack(ResettableCallBack l) {
        listener = l;
    }

    public ResetableTotalWeight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ComponentResettableTotalWeight,
                0, 0);
        try {

            showAlertDialog = a.getBoolean(R.styleable.ComponentResettableTotalWeight_showAlertDialogBeforeReset,true);
            resettable = a.getBoolean(R.styleable.ComponentResettableTotalWeight_resettable, true);
            colorChoice = a.getInteger(R.styleable.ComponentResettableTotalWeight_componentColor, 0);
            textSize = a.getInteger(R.styleable.ComponentResettableTotalWeight_displaySize, 24);
            textSize = Integer.valueOf(PrefsRepo.getTripTotalWeightMeterTextSize());

            if(textSize<=1)
                textSize = 24;


            Log.d(TAG, " xml values off " + textSize + "  " + resettable + " colorChoice " + colorChoice);
        } finally {
            a.recycle();
        }
        init();
        setColor(colorChoice);
        setVisibility();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        v = inflater.inflate(R.layout.component_resetable_totalweigh, this);
        tripMeterImage = v.findViewById(R.id.resetTripTotalMeter);
        txtResettableTotalWeight = v.findViewById(R.id.txtResettableTotalWeight);
        txtResettableTotalWeight.setTextSize(textSize);
        if (resettable) {
            Log.d(TAG, " add click listener");
            txtResettableTotalWeight.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "click ----> "+showAlertDialog);

        if(showAlertDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.resettable_total_weight))
                    .setMessage(context.getString(R.string.reset_total_weight))
                    .setPositiveButton(context.getString(R.string.txt_positive_button_title_yes), (dialog, which) -> {
                        if (listener != null) {
                            listener.onResetTotalWeight();
                        }
                    }).setNegativeButton(context.getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss());
            AlertDialog d = builder.create();
            d.show();
        }else{
            if (listener != null) {
                listener.onResetTotalWeight();
            }
        }

    }

    public void setVisibility() {
        if (PrefsRepo.isTripTotalMeter(context)) {
            tripMeterImage.setVisibility(VISIBLE);
            txtResettableTotalWeight.setVisibility(VISIBLE);
            txtResettableTotalWeight.setEnabled(true);
        } else {
            txtResettableTotalWeight.setVisibility(INVISIBLE);
            tripMeterImage.setVisibility(INVISIBLE);
            txtResettableTotalWeight.setEnabled(false);
        }
        invalidate();
    }

    private void setColor(int color) {
        if (color == 0) {
            tripMeterImage.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            txtResettableTotalWeight.setTextColor(Color.parseColor("#ffffff"));// blue intermercato color
        } else if (color == 1) {
            tripMeterImage.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            txtResettableTotalWeight.setTextColor(Color.parseColor("#041326"));// blue intermercato color
        } else if (color == 2) {
            tripMeterImage.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey), android.graphics.PorterDuff.Mode.MULTIPLY);
            txtResettableTotalWeight.setTextColor(Color.parseColor("#8d8c8d"));// blue intermercato color
        }
    }

    public void setNewTotalWeight(int totalWeight) {
        txtResettableTotalWeight.setText(WeightUtils.formatWeightWithMassUnit(true, totalWeight));
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }
    // Vector Drawable
    //setColorFilter(ContextCompat.getColor(context, R.color.COLOR_YOUR_COLOR), android.graphics.PorterDuff.Mode.SRC_IN);
}
