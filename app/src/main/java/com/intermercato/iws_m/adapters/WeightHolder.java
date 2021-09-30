package com.intermercato.iws_m.adapters;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intermercato.iws_m.Constants;
import com.intermercato.iws_m.R;
import com.intermercato.iws_m.realmModels.Row;
import com.intermercato.iws_m.utils.DateUtils;
import com.intermercato.iws_m.utils.WeightUtils;


public class WeightHolder extends RecyclerView.ViewHolder {

    private TextView netWeight, startTime,rowIndex;
    private ImageButton status;

    public WeightHolder(@NonNull View itemView) {
        super(itemView);
        netWeight = itemView.findViewById(R.id.txtNet);
        startTime = itemView.findViewById(R.id.txtStartTime);
        rowIndex = itemView.findViewById(R.id.rowIndex);
        status = itemView.findViewById(R.id.imgServerStatus);
    }

    public void bind(Row weight) {

        Log.d("row","info "+weight.toString());
        rowIndex.setText(String.valueOf(weight.getIndexOfRow()));
        startTime.setText(DateUtils.getFormattedDate(weight.getTimeStart(),"yyyy-MM-dd kk:mm:ss"));
        netWeight.setText(WeightUtils.formatWeightWithMassUnit(false, weight.getWeight()));

        if(weight.getDidGetSent() == Constants.DID_GET_SENT){
            status.setVisibility(View.VISIBLE);
        }else{
            status.setVisibility(View.INVISIBLE);
        }



    }
}
