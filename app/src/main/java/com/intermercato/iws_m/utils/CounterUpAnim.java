package com.intermercato.iws_m.utils;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.widget.TextView;


public class CounterUpAnim {


    public static void doIt(TextView v, int from, int to) {


        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(from, to);
        animator.addUpdateListener(animation -> v.setText(String.format("%s ", WeightUtils.formatWeightWithMassUnit(false,(int)animation.getAnimatedValue()))));
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }
}
