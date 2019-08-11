package com.psx.commons;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import java.lang.ref.WeakReference;

import timber.log.Timber;

class NetworkIndicatorOverlay {

    private String message;
    private long duration;
    private WeakReference<Activity> activityWeakReference;
    private Handler handler;
    private View inflatedView;
    private Animation showAnimation;
    private Animation hideAnimation;

    // Touch Listener
    private float startX;
    private float startY;

    private NetworkIndicatorOverlay(Activity activity, String message, long duration) {
        this.message = message;
        this.duration = duration;
        this.handler = new Handler();
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public static NetworkIndicatorOverlay make(MainApplication mainApplication, String message, long duration) {
        NetworkIndicatorOverlay indicatorOverlay = new NetworkIndicatorOverlay(mainApplication.getCurrentActivity(), message, duration);
        indicatorOverlay.inflateAndAttachView(mainApplication);
        indicatorOverlay.loadAnimations();
        return indicatorOverlay;
    }

    public void show() {
        if (activityWeakReference != null && activityWeakReference.get() != null) {
            inflatedView.startAnimation(showAnimation);
            handler.postDelayed(this::hide, duration);
        }
    }

    private void inflateAndAttachView(MainApplication mainApplication) {
        inflatedView = LayoutInflater.from(mainApplication.getCurrentActivity()).inflate(R.layout.network_indicator_overlay,
                mainApplication.getCurrentActivity().findViewById(android.R.id.content), false);
        ((AppCompatTextView) inflatedView.findViewById(R.id.network_indicator_message)).setText(message);
        inflatedView.findViewById(R.id.network_indicator_message).setOnClickListener(view -> {
            mainApplication.getEventBus().send(createClickOnNetworkObjectForEventBus());
            Toast.makeText(view.getContext(),"Clicked", Toast.LENGTH_SHORT).show();
        });
        ((FrameLayout) mainApplication.getCurrentActivity().findViewById(android.R.id.content)).addView(inflatedView);
    }

    private ExchangeObject createClickOnNetworkObjectForEventBus() {
        return new ExchangeObject.EventExchangeObject(Modules.PROJECT, Modules.COMMONS, CustomEvents.INTERNET_INFO_BANNER_CLICKED);
    }

    private void loadAnimations() {
        if (activityWeakReference != null && activityWeakReference.get() != null) {
            hideAnimation = AnimationUtils.loadAnimation(activityWeakReference.get(), R.anim.down_to_top);
            showAnimation = AnimationUtils.loadAnimation(activityWeakReference.get(), R.anim.top_to_down);
        }
    }

    private void hide() {
        if (inflatedView != null && activityWeakReference != null && activityWeakReference.get() != null) {
            inflatedView.startAnimation(hideAnimation);
            inflatedView = null;
        } else {
            Timber.e("Trying to call hide() on a null view NetworkIndicatorOverlay.");
        }
    }
}
