package com.samagra.parent.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.R2;
import com.samagra.commons.Constants;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.base.BaseActivity;

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

    private Unbinder unbinder;

    @Inject
    SplashPresenter<SplashContract.View, SplashContract.Interactor> splashPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);
        getActivityComponent().inject(this);
        splashPresenter.onAttach(this);
        splashPresenter.requestStoragePermissions();
    }


    @Override
    public void endSplashScreen() {
        splashPresenter.moveToNextScreen();
    }

    /**
     * This function configures the Splash Screen
     * and renders it on screen. This includes the Splash screen image and other UI configurations.
     *
     */
    @Override
    public void showSimpleSplash() {
        splashImage.setImageResource(R.drawable.login_bg);
        splashImage.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(this::endSplashScreen, SPLASH_TIMEOUT);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    /**init();
        }
     * This function sets the activity layout and binds the UI Views.
     * This function should be called after the relevant permissions are granted to the app by the user
     */
    @Override
    public void showActivityLayout() {
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);
//        Button bb = findViewById(R.id.decrdc);
//        bb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StudentDetailsComponentManager.registerProfilePackage(new StudentDetailsSectionInteractor());
//                IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
//                iStudentDetailsContract.launchProfileActivity(getActivityContext(), com.samagra.parent.R.id.fragment_container, getSupportFragmentManager());
//            }
//        });
    }

    @Override
    public void redirectToHomeScreen() {
        Grove.d("Redirecting to Home screen from Splash screen >>> ");
        Intent intent = new Intent(Constants.INTENT_LAUNCH_HOME_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Grove.d("Closing Splash Screen");
        finishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
        splashPresenter.onDetach();
    }

    @Override
    public void setupToolbar() {

    }
}
