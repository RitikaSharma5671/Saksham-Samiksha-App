package com.example.assets.uielements;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.NumberFormat;
import java.util.Locale;

public class ArcAngleAnimation extends Animation {

    private ArcProgress arcView;//Arc view oject
    private float oldAngle;//old angle
    private int progress;//progress of the arc
    private final float multiplier = 27 / 10f;//Multiplier (as we are taking initial angle from 135 degrees to 270 degrees)
    private int mBalance;//balance

    /**
     * Constructor
     *
     * @param arcView
     * @param progress
     * @param mBalance
     */
    public ArcAngleAnimation(ArcProgress arcView, int progress, int mBalance) {
        //assigning the values
        this.mBalance = mBalance;
        this.oldAngle = arcView.getmArcAngle();
        this.arcView = arcView;
        this.progress = progress;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = (progress * multiplier) * interpolatedTime;
        arcView.setmArcAngle(angle);//change the angle and call onDraw()
        arcView.setmRewardEarned(addCommasToNumber((int) (mBalance * interpolatedTime)));
        arcView.requestLayout();
    }
    public static String addCommasToNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}

