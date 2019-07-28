package com.psx.odktest.ui.SplashScreen;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

import org.odk.collect.android.ODKDriver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    private static final int SPLASH_TIMEOUT = 2000; // milliseconds

    @BindView(R.id.splash)
    public ImageView splashImage;
    @BindView(R.id.splash_default)
    public LinearLayout splashDefaultLayout;

    private Unbinder unbinder;

    @Inject
    SplashPresenter<SplashContract.View, SplashContract.Interactor> splashPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        splashPresenter.onAttach(this);
        splashPresenter.requestStoragePermissions();
    }


    @Override
    public void endSplashScreen() {
        AncillaryScreensDriver.launchLoginScreen(this);
        finish();
    }

    @Override
    public void showSimpleSplash() {
        splashDefaultLayout.setVisibility(View.GONE);
        splashImage.setImageResource(ODKDriver.getSplashScreenImageRes());
        splashImage.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(this::endSplashScreen, SPLASH_TIMEOUT);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showActivityLayout() {
        setContentView(R.layout.splash_screen);
        unbinder = ButterKnife.bind(this);
        splashPresenter.startGetFormListCall();
        splashPresenter.startUnzipTask();
    }

    @Override
    public void setupToolbar() {
        // No toolbar required since full screen activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        splashPresenter.onDetach();
    }
}
