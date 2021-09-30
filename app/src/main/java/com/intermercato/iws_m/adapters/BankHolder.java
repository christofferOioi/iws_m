package com.intermercato.iws_m.adapters;


import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.intermercato.iws_m.R;
import com.intermercato.iws_m.realmModels.Bank;
import com.intermercato.iws_m.repositories.PrefsRepo;
import com.intermercato.iws_m.utils.WeightUtils;


/**
 * Created by ch on 2018-08-13.
 */

public class BankHolder extends RecyclerView.ViewHolder {

    public TextView title,nor,totweight;
    public ConstraintLayout holder;
    public ImageView arrow;
    public View plate, activePlate;

    public BankHolder(View v) {
        super(v);

        totweight = v.findViewById(R.id.txtTotalBankWeight);
        title = v.findViewById(R.id.bankName);
        holder = v.findViewById(R.id.showWeightHolder);
        plate = v.findViewById(R.id.bgPlate);
        arrow = v.findViewById(R.id.arrowIcon);
        nor = v.findViewById(R.id.tvNumberOfRows);
        activePlate = v.findViewById(R.id.activePlate);

    }

    public void bind(Bank b, double maxWeightVal, int bankWidth, int bankHeight) {
       // Log.d("bankHolder","m  "+b.getBankname()+"    "+b.getTotalweight()+ "  " +maxWeightVal+"   "+bankWidth+"   "+bankHeight);
        Log.d("Adapter","bind viewholder");
        ConstraintSet set = new ConstraintSet();

        totweight.setText(String.valueOf(b.getTotalWeight()));

        //totweight.setText(String.format("%s %d %s", itemView.getContext().getString(R.string.txt_sum), WeightUtils.formatWeight(b.getTotalweight()), PrefsRepo.getWeightUnit()));
        totweight.setText(String.format("%s %s ",/* itemView.getContext().getString(R.string.txt_sum),*/ WeightUtils.formatWeight(false,b.getTotalWeight()), PrefsRepo.getWeightUnit()));
        title.setText(b.getAlias());
        nor.setText(String.valueOf(b.getRows().size()));

        /*if(b.getTotalweight()> 0.0) {
            int heightVal;
            heightVal = (int) ((b.getTotalweight() / maxWeightVal) * 120);
            Log.d("bankHolder", "heightVal " + heightVal+ "<-  "+b.getTotalweight()+"  "+maxWeightVal);
            if(heightVal>=120)
                return;
            Log.d("bankHolder", "---->");
            set.clone(holder);
            set.constrainHeight(R.id.bgPlate, (int) heightVal);
            set.connect(R.id.bgPlate, ConstraintSet.BOTTOM,R.id.showWeightHolder, ConstraintSet.BOTTOM,0);
            set.applyTo(holder);
        } else {
            set.constrainHeight(R.id.bgPlate,  (int) 0);
            set.applyTo(holder);

        }*/
    }


}
