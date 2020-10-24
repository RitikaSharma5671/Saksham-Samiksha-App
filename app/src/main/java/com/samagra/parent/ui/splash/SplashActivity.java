package com.samagra.parent.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.samagra.ancillaryscreens.screens.login.LoginActivity;
import com.samagra.commons.CommonUtilities;
import com.samagra.commons.Constants;
import com.samagra.commons.MainApplication;
import com.samagra.commons.ScreenChangeEvent;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.R;
import com.samagra.parent.base.BaseActivity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The View Part for the Splash Screen, must implement {@link SplashContract.View}
 * This Activity needs to be declared as the launcher activity in the AndroidManifest.xml
 *
 * @author Pranav Sharma
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {

    public LinearLayout splashDefaultLayout;
    public ImageView splashImage;
    private static CompositeDisposable screenChangeDisposable;
    @Inject
    SplashPresenter<SplashContract.View, SplashContract.Interactor> splashPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getActivityComponent().inject(this);
        splashPresenter.onAttach(this);
        splashImage = findViewById(R.id.splash);
        splashDefaultLayout = findViewById(R.id.splash_default);
        splashImage.setImageResource(R.drawable.login_bg);
        splashImage.setVisibility(View.VISIBLE);
        splashPresenter.initialise((MainApplication) getApplicationContext());
        splashPresenter.requestStoragePermissions(getActivityContext().getPackageName(), getActivityContext().getPackageManager(), getActivityContext());
        screenChangeDisposable = new CompositeDisposable();
        setDisposable();
    }


    @Override
    public void endSplashScreen() {
        Grove.d("Moving to next screen from Splash");
        if (!splashPresenter.getIFormManagementContract().isScopedStorageUsed()) {
            splashPresenter.getIFormManagementContract().enableUsingScopedStorage();
        }
            if (splashPresenter.getMvpInteractor().isLoggedIn()) {
                if (splashPresenter.canLaunchHome()) {
                    if (splashPresenter.isJwtTokenValid()) {
                        splashPresenter.getIFormManagementContract().resetODKForms(getActivityContext(), failedResetActions -> {
                            Grove.d("Failure to reset actions at Splash screen " + failedResetActions);
                            redirectToHomeScreen();
                        });

                    } else {
                        splashPresenter.updateJWT(getActivityContext().getResources().getString(R.string.fusionauth_api_key));
                    }
                }
            } else {
                new CountDownTimer(2500, 500) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        launchLoginScreen();
                    }
                }.start();
                Grove.d("Closing Splash Screen and Launching Login");

        }
    }
    /**
     * This function configures the Splash Screen
     * and renders it on screen. This includes the Splash screen image and other UI configurations.
     */
    @Override
    public void showSimpleSplash() {
        splashImage.setImageResource(R.drawable.login_bg);
        splashImage.setVisibility(View.VISIBLE);
        if (!splashPresenter.getMvpInteractor().isLoggedIn()) {
            new CountDownTimer(2500, 500) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    launchLoginScreen();
                }
            }.start();

        } else {
            splashPresenter.verifyJWTTokenValidity(getActivityContext().getResources().getString(R.string.fusionauth_api_key), getActivityContext());
        }
    }

    @Override
    public void finishSplashScreen() {
        finish();
    }

    /**
     * This function sets the activity layout and binds the UI Views.
     * This function should be called after the relevant permissions are granted to the app by the user
     */
    @Override
    public void showActivityLayout() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenChangeDisposable.dispose();
        splashPresenter.onDetach();
    }

    private void setDisposable() {
        screenChangeDisposable
                .add(((MainApplication) (getApplicationContext()))
                        .eventBusInstance()
                        .register(ScreenChangeEvent.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            if (event.getDestinationScreen().equals("Login")) {
                                launchLoginScreen();
                            } else if (event.getDestinationScreen().equals("Home")) {
                                redirectToHomeScreen();
                            }
                        }));
    }

    public void launchLoginScreen() {
        splashPresenter.getMvpInteractor().getPreferenceHelper().updateInstallSendCOunt(false);
        splashPresenter.getIFormManagementContract().enableUsingScopedStorage();
        splashPresenter.getIFormManagementContract().resetPreviousODKForms(failedResetActions -> {
            Grove.d("Failure to reset actions at Splash screen " + failedResetActions);
            splashPresenter.setInclompleteProfileCount();
            Intent intent = new Intent(this, LoginActivity.class);
            CommonUtilities.startActivityAsNewTask(intent, this);
            finishSplashScreen();
        });

    }

    @Override
    public void redirectToHomeScreen() {
        splashPresenter.getMvpInteractor().getPreferenceHelper().updateInstallSendCOunt(false);
        splashPresenter.getIFormManagementContract().enableUsingScopedStorage();
        Grove.d("Redirecting to Home screen from Splash screen >>> ");
        splashPresenter.setInclompleteProfileCount();
        Intent intent = new Intent(Constants.INTENT_LAUNCH_HOME_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Grove.d("Closing Splash Screen");
        finishSplashScreen();
    }


    @Override
    public void setupToolbar() {

    }
}
