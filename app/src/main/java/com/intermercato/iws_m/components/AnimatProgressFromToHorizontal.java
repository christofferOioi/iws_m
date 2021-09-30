package com.intermercato.iws_m.components;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimatProgressFromToHorizontal extends Animation {
    private View mView;
    private int mFromX;
    private int mToX;

    public AnimatProgressFromToHorizontal(View progressBar, int from, int to) {
        super();
        this.mView = progressBar;
        this.mFromX = from;
        this.mToX = to;
    }



    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth;

        if (mView.getWidth() != mToX) {
            newWidth = (int) (mFromX + ((mToX - mFromX) * interpolatedTime));
            mView.getLayoutParams().width = newWidth;
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
