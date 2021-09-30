/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intermercato.iws_m.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;


import com.intermercato.iws_m.utils.Primitives;
import com.intermercato.iws_m.R;
import com.intermercato.iws_m.realmModels.Bank;
import com.intermercato.iws_m.realmModels.RealmRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import io.realm.OrderedRealmCollection;
import io.realm.Realm;



public class BanksRecyclerViewAdapter extends RealmRecyclerViewAdapter<Bank, BankHolder> {

    private BankOnClickCallback listener;
    private boolean inDeletionMode = false;
    private Set<Integer> countersToDelete = new HashSet<>();
    private ConstraintLayout.LayoutParams params;
    private double maxWeightVal;
    private int bankWidth;
    private int bankHeight;
    private boolean showHideBankArrow = false;
    private Context context;
    private boolean didclick = false;

    public BanksRecyclerViewAdapter(OrderedRealmCollection<Bank> data, Context c) {
        super(data, true);
        setMaxWeightVal();
        Log.d("Adapter", "data "+data.size());
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(false);
        context = c;

    }

    public void setLayoutParamsForHolder(ConstraintLayout.LayoutParams p, int w, int h) {
        params = p;
        bankWidth = w;
        bankHeight = h;
    }

    public void setClickListener(BankOnClickCallback l) {
        listener = l;

    }

    public void setShowBankArrow(boolean s) {
        showHideBankArrow = s;
    }


    public void setMaxWeightVal() {
        Log.d("Adapter", "calc maxweight");
        ArrayList<Integer> maxWeightValue = new ArrayList<>();
        if(getData()!=null) {
            for (Bank b : getData()) {
                maxWeightValue.add(b.getTotalWeight());
            }
            maxWeightVal = Primitives.maxIntList(maxWeightValue);
        }

        Log.d("Adapter", "maxWeightVal " + maxWeightValue + " " + maxWeightVal);
    }


    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<Integer> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public BankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Adapter", "onCreateViewHolder ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_mode_bank_row, parent, false);
        itemView.setLayoutParams(params);
        return new BankHolder(itemView);
    }



    @Override
    public void onBindViewHolder(BankHolder holder, int position) {
        Log.d("Adapter","onBindViewHolder");
        final Bank b = getItem(position);
        setMaxWeightVal();
        holder.bind(b, maxWeightVal, bankWidth, bankHeight);
        //Log.d("ADAPTER", "onBindViewHolder " + b.isActive());
        didclick = false;
        Log.d("Adapter","active "+b.getActive());
        if (showHideBankArrow)
            holder.arrow.setVisibility(b.getActive() == true ? View.VISIBLE : View.INVISIBLE);

       /* if (b.getActive() && b.getTotalweight() > 0) {
            holder.activePlate.setBackgroundColor(context.getResources().getColor(R.color.dark_grey*//*,context.getTheme()*//*));
            //setColor(holder.holder);
            Log.d("ADAPTER", "ORANGE");
        } else {
            //setColor(holder.holder);
            Log.d("ADAPTER", "BLUE " );
            holder.plate.setBackgroundColor(context.getResources().getColor(R.color.orange*//*,context.getTheme()*//*));
        }*/

        // holder.itemView.setLayoutParams(params);

        holder.itemView.setOnClickListener(v -> {
            didclick = true;
            listener.onBankClick(b.getId(),b.getTotalWeight(),position);

            if (showHideBankArrow) {
                for (Bank a : getData()) {
                    //Log.d("ADAPTER", "in  ---> " + a.isActive());
                    if (b.getId().equals(a.getId())) {
                        holder.arrow.setVisibility(View.VISIBLE);
                        dataHandler(a, true);
                    } else {
                        dataHandler(a, false);
                    }
                }
                notifyItemChanged(position);
            }
        });

        if (b.getActive() && !didclick) {
            Log.d("Adapter","is active "+b.getAlias());
            goUp(holder.totweight, b);
            if(listener!=null)
                listener.onActiveBankUpdated(b.getTotalWeight());

        }

        if(b.getActive()){
            holder.totweight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_grey)));
        } else{
            holder.totweight.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bar_chart_bg_staple)));
        }


        setCharts(getData(), b,holder.holder, holder.plate);
        Log.d("Adapter","onBind viewholder ends");
    }

    private void goUp(TextView totweight, Bank b) {
        if (b.getTotalWeight() > 0) {
            //CounterUpAnim.doIt(totweight, (b.getTotalWeight() - b.getRows().last().getWeight()), b.getTotalWeight());
        }
    }

    private void setCharts(OrderedRealmCollection<Bank> banks, Bank b, ConstraintLayout bankHolder, View bgPlate) {



            ConstraintSet set = new ConstraintSet();
            if (b.getTotalWeight() > 0.0) {
                int heightVal = (int) ((b.getTotalWeight() / maxWeightVal) * 110);
                Log.d("bankHolder", "heightVal " + b.getAlias() + "  " + heightVal + "<-  " + b.getTotalWeight() + "  " + maxWeightVal);
                if (heightVal >= 110) {
                    heightVal = 110;
                }

                set.clone(bankHolder);
                set.constrainHeight(bgPlate.getId(), (int) heightVal);
                set.connect(bgPlate.getId(), ConstraintSet.BOTTOM, R.id.showWeightHolder, ConstraintSet.BOTTOM, 0);
                set.applyTo(bankHolder);

            } else {
                Log.d("bankHolder", "oh no rows");

                set.clone(bankHolder);
                set.constrainHeight(bgPlate.getId(), (int) 1);
                set.connect(bgPlate.getId(), ConstraintSet.BOTTOM, R.id.showWeightHolder, ConstraintSet.BOTTOM, 0);
                set.applyTo(bankHolder);
            }



    }

    private void dataHandler(Bank a, boolean active) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> a.setActive(active));
    }

    private void setColor(ConstraintLayout cl) {

    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return 0;//getItem(index).getId();
    }


    //holder.data = obj;
    //final String itemId = obj.getId();
    //noinspection ConstantConditions
    //holder.ttitle.setText(obj.getBankName());
    //holder.deletedCheckBox.setChecked(countersToDelete.contains(itemId));
/*        if (inDeletionMode) {
            holder.deletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        countersToDelete.add(itemId);
                    } else {
                        countersToDelete.remove(itemId);
                    }
                }
            });
        } else {holder.deletedCheckBox.setOnCheckedChangeListener(null);
        }*/
    //holder.deletedCheckBox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);


}
