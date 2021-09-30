package com.intermercato.iws_m.components;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.intermercato.iws_m.R;
import com.intermercato.iws_m.mApplication;


public class ProgressBarWithTextProgress extends ConstraintLayout {


    private boolean showTextProgress = true;
    private int direction = 0;
    private float parentWidth = 0;
    private float parentHeight = 0;
    private int totalweight = 0;

    private float verticalStepValue = 0;
    private float horizontalStepValue = 0;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private int lastValue = 2;
    private int startValue = 0;
    private int lastPercentValue = 0;
    private boolean maxWeightHasBeenReached = false;
    private boolean shouldAnimateProgress = false;
    private final String TAG = "progressbar";
    private View bar;
    private TextView displayPercent, displayInfo;
    private Context ctx;
    private LayoutParams displayParams;
    private LayoutParams barParams;
    private boolean containerIsSet = false;
    private boolean barIsSet = false;
    private int lastTotalWeight =0;
    private OnViewFinished listener;

    public void setListener(OnViewFinished l){
        listener = l;
    }

    public interface OnViewFinished {
        public void onProgressMaxWeightViewFinished();
    }

    public void setComponentWidth(int _parentWidth, int _parentHeight) {
        Log.d(TAG, "parentWidth: " + _parentWidth + " _parentHeight " + _parentHeight);
        parentWidth = _parentWidth;
        parentHeight = _parentHeight - 10;
        horizontalStepValue = (parentWidth / 100);
        verticalStepValue = (parentHeight / 100);
        Log.d(TAG, "horizontalStepValue: " + horizontalStepValue + " verticalStepValue " + verticalStepValue);
    }

    public void setMaxTotalWeight(int weight) {
        totalweight = weight;
    }

    public void resetProgress() {

        displayInfo.setText("");
        displayInfo.setVisibility(INVISIBLE);
        displayPercent.setText("");
        maxWeightHasBeenReached = false;
        if (direction == HORIZONTAL) {
            updateHorizontal(startValue);
        } else if (direction == VERTICAL) {
            Log.d(TAG, "vertical");
            updateVertical(startValue);
        }
    }

    public ProgressBarWithTextProgress(Context context) {
        super(context);
        ctx = context;
    }

    public ProgressBarWithTextProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ComponentProgressBar, 0, 0);
        try {
            showTextProgress = a.getBoolean(R.styleable.ComponentProgressBar_showTextProgress, true);
            shouldAnimateProgress = a.getBoolean(R.styleable.ComponentProgressBar_animateProgress, true);
            direction = a.getInteger(R.styleable.ComponentProgressBar_direction, HORIZONTAL);
            Log.d(TAG, "init show text progress: " + showTextProgress + " direction: " + direction);
        } finally {
            a.recycle();
            init(direction);

        }
    }

    public ProgressBarWithTextProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(final int direction) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int layout = 0;
        if (direction == HORIZONTAL) {
            layout = R.layout.component_progressbar_with_text_progress;
        } else if (direction == VERTICAL) {
            layout = R.layout.component_progressbar_with_text_progress_vertical;
        }

        View view = inflater.inflate(layout, this);
        // Get view instances

        bar = view.findViewById(R.id.bar);
        displayPercent = view.findViewById(R.id.displayPercent);
        displayInfo = view.findViewById(R.id.displayInfo);

        final ConstraintLayout comp_container = view.findViewById(R.id.comp_container);
        comp_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                comp_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setComponentWidth(comp_container.getWidth(), comp_container.getHeight());
                containerIsSet = true;
            }
        });

        bar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                bar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "bar --> " + bar.getWidth() + "   " + bar.getHeight());
                if (direction == HORIZONTAL) {
                    lastValue = bar.getWidth();
                    startValue = lastValue;
                } else if (direction == VERTICAL) {
                    lastValue = bar.getHeight();
                    startValue = lastValue;
                }
                barIsSet = true;
                if(lastTotalWeight > 0){
                    setCurrentWeight(lastTotalWeight);
                }
                if(listener!=null)
                    listener.onProgressMaxWeightViewFinished();
            }
        });

    }


    public void setCurrentWeight(int currentTotalWeight) {
        lastTotalWeight = currentTotalWeight;
        Log.d(TAG, "currentWeight: " + currentTotalWeight + " total " + totalweight);
        if(!barIsSet || !containerIsSet){
            return;
        }

        if (totalweight <= 0) {
            return;
        }

        if (totalweight < currentTotalWeight) {
            displayInfo.setVisibility(VISIBLE);
            int overload = totalweight - currentTotalWeight;
            displayInfo.setText(mApplication.Companion.applicationContext().getString(R.string.max_weight_reached) + overload);
        }

        if (maxWeightHasBeenReached) {
            return;
        }

        displayParams = (LayoutParams) displayPercent.getLayoutParams();
        barParams = (LayoutParams) bar.getLayoutParams();

        if (totalweight < currentTotalWeight) {
            //Log.d(TAG, "maxWeight is reached");
            maxWeightHasBeenReached = true;
        }


        int percentValue = Math.round(100 * ((float) currentTotalWeight / (float) totalweight));
        //Log.d(TAG, "percent: " + (percentValue));
        //Log.d(TAG, "currentWeight: " + currentTotalWeight + " stepval " + horizontalStepValue + " totalweight " + totalweight + " percentval " + percentValue);

        if (percentValue >= 100) {
            percentValue = 100;
            displayInfo.setVisibility(VISIBLE);
            displayInfo.setText("Max weight reached!");

        }

        if (!shouldAnimateProgress) {
            displayPercent.setText(String.format("%1$d %%", percentValue));
        } else {
            animatePercent(displayPercent, lastPercentValue, percentValue);
            lastPercentValue = percentValue;
        }


        if (direction == HORIZONTAL) {
            updateHorizontal((int) (horizontalStepValue * percentValue));
            Log.d(TAG, "HORIZONTAL ");
        }

        if (direction == VERTICAL) {
            updateVertical((int) (verticalStepValue * percentValue));
            Log.d(TAG, "VERTICAL ");
        }

    }

    private void updateHorizontal(int stepValue) {


        if (!shouldAnimateProgress) {
            barParams.width = stepValue;
        } else {
            Animation animation = new AnimatProgressFromToHorizontal(bar, lastValue, stepValue);
            lastValue = stepValue;
            animation.setFillAfter(true);
            animation.setFillBefore(true);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {


                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(800);

            bar.setAnimation(animation);
            bar.startAnimation(animation);

        }

      /*  displayPercent.setLayoutParams(displayParams);

        ConstraintLayout constraintLayout = findViewById(R.id.comp_container);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(R.id.displayPercent, ConstraintSet.BOTTOM, R.id.bar, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.displayPercent, ConstraintSet.END, R.id.bar, ConstraintSet.END, 0);
        constraintSet.applyTo(constraintLayout);*/
    }

    private void updateVertical(int stepValue) {
        Log.d(TAG, " step " + stepValue + "  " + lastValue);


        if (!shouldAnimateProgress) {
            barParams.height = stepValue;
        } else {

            Animation animation = new AnimatProgressFromToVertical(bar, lastValue, stepValue);
            lastValue = stepValue;
            animation.setFillAfter(true);
            animation.setFillBefore(true);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {


                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(800);

            bar.setAnimation(animation);
            bar.startAnimation(animation);
        }
 /*       displayPercent.setLayoutParams(displayParams);

        ConstraintLayout constraintLayout = findViewById(R.id.comp_container);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(R.id.displayPercent, ConstraintSet.TOP, R.id.bar, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.displayPercent, ConstraintSet.START, R.id.bar, ConstraintSet.END, 0);
        constraintSet.applyTo(constraintLayout);
*/

    }


    public static void animatePercent(final TextView v, int from, int to) {


        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setText(String.format("%1$d %%", (int) animation.getAnimatedValue()));
            }
        });

        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }
}
