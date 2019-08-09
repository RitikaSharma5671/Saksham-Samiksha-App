package com.psx.ancillaryscreens.screens.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.psx.ancillaryscreens.R;
import com.psx.ancillaryscreens.R2;
import com.psx.ancillaryscreens.base.BaseActivity;
import com.psx.commons.MainApplication;

import org.odk.collect.android.ODKDriver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * The View Part for the Splash Screen, must implement {@link SplashContract.View}
 * This Activity needs to be declared as the launcher activity in the AndroidManifest.xml
 *
 * @author Pranav Sharma
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {

    private static final int SPLASH_TIMEOUT = 2000; // milliseconds

    @BindView(R2.id.splash)
    public ImageView splashImage;
    @BindView(R2.id.splash_default)
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
        splashPresenter.moveToNextScreen();
        finish();
    }

    /**
     * This function configures the Splash Screen through the values provided to the {@link org.odk.collect.android.ODKDriver}
     * and renders it on screen. This includes the Splash screen image and other UI configurations.
     *
     * @see org.odk.collect.android.ODKDriver#init(MainApplication, int, int, int, int, long)
     */
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

    /**
     * This function sets the activity layout and binds the UI Views.
     * This function should be called after the relevant permissions are granted to the app by the user
     */
    @Override
    public void showActivityLayout() {
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);
        splashPresenter.startGetFormListCall();
        splashPresenter.startUnzipTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        splashPresenter.onDetach();
    }
}
