package com.intermercato.iws_m.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intermercato.iws_m.R;
import com.intermercato.iws_m.utils.WeightUtils;


/**
 * Created by fredrik on 15-08-06.
 */
public class DisplaySessionWeights extends RelativeLayout {

    private int bruttoWeight = 0;
    private int containerWeight = 0;
    private int nettoWeight = 0;
    private int counterUp = 0;
    private View view;
    private TextView brutto, container, netto, txtCounter;
    private Context context;

    public DisplaySessionWeights(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DisplaySessionWeights(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setWeightsArray(int b, int c, int n, int counter) {
        bruttoWeight = b;
        containerWeight = c;
        nettoWeight = n;
        counterUp = counter;
        setWeight();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.component_session_weights, this);
        brutto = view.findViewById(R.id.txtBrutto);
        container = view.findViewById(R.id.txtContainer);
        netto = view.findViewById(R.id.txtNetto);
        txtCounter = view.findViewById(R.id.txtCountChamber);
    }

    private void setWeight() {

        txtCounter.setText("" + counterUp);

        String totalweight1 = WeightUtils.formatWeight(false, bruttoWeight);
        String strB = "B: " + String.valueOf(totalweight1);
        brutto.setText(strB);

        String totalweight2 = WeightUtils.formatWeight(false, containerWeight);
        String strC = "C: " + String.valueOf(totalweight2);
        container.setText(strC);

        String totalweight3 = WeightUtils.formatWeight(false, nettoWeight);
        String strBruto = "N: " + String.valueOf(totalweight3);
        netto.setText(strBruto);

    }

    @Override
    public void invalidate() {
        super.invalidate();
        setWeight();
    }
}
