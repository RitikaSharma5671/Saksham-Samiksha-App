package com.psx.commons;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;

import java.lang.ref.WeakReference;

class NetworkIndicatorOverlay {

    private String message;
    private long duration;
    private WeakReference<Activity> activityWeakReference;
    private Handler handler;
    private View inflatedView;
    private Animation showAnimation;
    private Animation hideAnimation;

    private NetworkIndicatorOverlay(Activity activity, String message, long duration) {
        this.message = message;
        this.duration = duration;
        this.handler = new Handler();
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public static NetworkIndicatorOverlay make(Activity activity, String message, long duration) {
        NetworkIndicatorOverlay indicatorOverlay = new NetworkIndicatorOverlay(activity, message, duration);
        indicatorOverlay.inflateAndAttachView(activity);
        indicatorOverlay.loadAnimations();
        return indicatorOverlay;
    }

    private void loadAnimations() {
        if (activityWeakReference != null && activityWeakReference.get() != null) {
            hideAnimation = AnimationUtils.loadAnimation(activityWeakReference.get(), R.anim.down_to_top);
            showAnimation = AnimationUtils.loadAnimation(activityWeakReference.get(), R.anim.top_to_down);
        }
    }

    private void inflateAndAttachView(Activity activity) {
        inflatedView = LayoutInflater.from(activity).inflate(R.layout.network_indicator_overlay,
                activity.findViewById(android.R.id.content), false);
        ((AppCompatTextView) inflatedView.findViewById(R.id.network_indicator_message)).setText(message);
        ((FrameLayout) activity.findViewById(android.R.id.content)).addView(inflatedView);
    }

    public void show() {
        if (activityWeakReference != null && activityWeakReference.get() != null) {
            inflatedView.startAnimation(showAnimation);
            handler.postDelayed(this::hide, duration);
        }
    }

    private void hide() {
        if (inflatedView != null && activityWeakReference != null && activityWeakReference.get() != null) {
            inflatedView.startAnimation(hideAnimation);
        }
    }
}
