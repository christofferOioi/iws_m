package com.intermercato.iws_m.components;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimatProgressFromToVertical extends Animation {
    private View mView;
    private int mFromHeight;
    private int mToHeight;

    public AnimatProgressFromToVertical(View progressBar, int from, int to) {
        super();
        this.mView = progressBar;
        this.mFromHeight = from;
        this.mToHeight = to;
    }



    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;

        if (mView.getHeight() != mToHeight) {
            newHeight = (int) (mFromHeight + ((mToHeight - mFromHeight) * interpolatedTime));
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
