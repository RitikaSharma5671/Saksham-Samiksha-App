package com.samagra.parent.ui.HomeScreen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.example.update.UpdateApp;
import com.google.android.material.snackbar.Snackbar;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.models.AboutBundle;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.commons.Constants;
import com.samagra.commons.CustomEvents;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.FormFilledEvent;
import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.InternetMonitor;
import com.samagra.commons.MainApplication;
import com.samagra.commons.Modules;
import com.samagra.grove.logging.Grove;
import com.samagra.notification_module.AppNotificationUtils;
import com.samagra.parent.AppConstants;
import com.samagra.parent.R;
import com.samagra.parent.UtilityFunctions;
import com.samagra.parent.base.BaseActivity;
import com.samagra.parent.ui.Settings.UpdateAppLanguageFragment;
import com.samagra.user_profile.contracts.ComponentManager;
import com.samagra.user_profile.contracts.IProfileContract;
import com.samagra.user_profile.contracts.ProfileSectionInteractor;
import com.samagra.user_profile.profile.UserProfileElement;

import org.odk.collect.android.utilities.LocaleHelper;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.odk.collect.android.preferences.GeneralKeys.KEY_APP_LANGUAGE;

/**
 * View part of the Home Screen. This class only handles the UI operations, all the business logic is simply
 * abstracted from this Activity. It <b>must</b> implement the {@link HomeMvpView} and extend the {@link BaseActivity}
 *
 * @author Pranav Sharma
 */
public class HomeActivity extends BaseActivity implements HomeMvpView, View.OnClickListener {

    @BindView(R.id.fill_forms)
    public LinearLayout fillFormLayout;
    @BindView(R.id.view_submitted_forms)
    public LinearLayout viewSubmittedLayout;
    @BindView(R.id.submit_forms)
    public LinearLayout submitFormLayout;
    @BindView(R.id.need_help)
    public LinearLayout helplineLayout;
    @BindView(R.id.parent)
    public RelativeLayout parent;
    @BindView(R.id.progress_bar_layout)
    public RelativeLayout progressBarLayout;
    @BindView(R.id.progress_bar_text)
    public TextView progressBarText;
    @BindView(R.id.lottie_loader)
    public LottieAnimationView lottie_loader;
    @BindView(R.id.parentHome)
    public LinearLayout parentHome;
    @BindView(R.id.welcome_text)
    public TextView welcomeText;

    private Disposable logoutListener = null;
    private static CompositeDisposable formSentDisposable = new CompositeDisposable();
    private PopupMenu popupMenu;
    private Snackbar messageView = null;
    private Unbinder unbinder;
    UpdateApp mUpdateApp;

    @Inject
    HomePresenter<HomeMvpView, HomeMvpInteractor> homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        homePresenter.onAttach(this);
        setupToolbar();
        homePresenter.applySettings();
        InternetMonitor.startMonitoringInternet(((MainApplication) getApplicationContext()));
        setupListeners();
        homePresenter.updateLanguageSettings();
        AppNotificationUtils.updateFirebaseToken(getActivityContext(), AppConstants.BASE_API_URL, getActivityContext().getResources().getString(R.string.fusionauth_api_key));
        mUpdateApp = new UpdateApp(this);
        renderLayoutInvisible();
    }

    private void relaunchHomeScreen() {
        new LocaleHelper().updateLocale(getActivityContext());
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
        finishAffinity();
    }

    @Override
    public void setDownloadProgress(int progress) {
        if (progress >= 100) {
            progressBarText.setText(R.string.hundred_percent);
        } else {
            progressBarText.setText(String.format(Locale.ENGLISH, "%d%%", progress));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        renderLayoutInvisible();
        homePresenter.fetchWelcomeText();
        homePresenter.resetProgressVariables();
        homePresenter.checkForFormUpdates();
        customizeToolbar();
        setDisposable();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.getBackStackEntryAt(0);
            if (fm.getBackStackEntryAt(0).getName() != null && fm.getBackStackEntryAt(0).getName().equals("UpdateAppLanguageFragment")) {
                fm.popBackStackImmediate();
                parentHome.setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void renderLayoutVisible() {
        parentHome.setVisibility(View.VISIBLE);
        startWalletLoader(false);
        progressBarLayout.setVisibility(View.GONE);
    }


    @Override
    public void renderLayoutInvisible() {
        progressBarLayout.setVisibility(View.VISIBLE);
        startWalletLoader(true);
        parentHome.setVisibility(View.GONE);
    }

    private void startWalletLoader(boolean b) {
        if (b) {
            lottie_loader.setVisibility(View.VISIBLE);
            lottie_loader.setAnimation("loader.json");
            lottie_loader.setRepeatCount(ValueAnimator.INFINITE);
            lottie_loader.playAnimation();
        } else {
            lottie_loader.cancelAnimation();
            lottie_loader.setVisibility(View.INVISIBLE);
        }
    }

    private void setupListeners() {
        fillFormLayout.setOnClickListener(this);
        viewSubmittedLayout.setOnClickListener(this);
        submitFormLayout.setOnClickListener(this);
        helplineLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill_forms:
                homePresenter.searchmodule();
                break;
            case R.id.view_submitted_forms:
                homePresenter.onViewSubmittedFormsOptionsClicked();
                break;
            case R.id.submit_forms:
                homePresenter.onSubmitFormsClicked();
                break;
            case R.id.need_help:
                homePresenter.onViewHelplineClicked();
                break;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void addFragment(int containerViewId, FragmentManager manager, Fragment fragment, String fragmentTag) {
        try {
            final String fragmentName = fragment.getClass().getName();
            Grove.d("addFragment() :: Adding new fragment %s", fragmentName);
            // Create new fragment and transaction
            final FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(containerViewId, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            new Handler().post(() -> {
                try {
                    transaction.commit();
                } catch (IllegalStateException ex) {
                    Grove.e("Failed to commit Fragment Transaction with exception %s", ex.getMessage());
                }
            });
        } catch (IllegalStateException ex) {
            Grove.e("Failed to add Fragment with exception %s", ex.getMessage());

        }

    }

    private void setDisposable() {
        formSentDisposable
                .add(((MainApplication) (getApplicationContext()))
                        .eventBusInstance()
                        .register(FormFilledEvent.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            String snackbarMessage = homePresenter.isNetworkConnected() ? getString(R.string.form_submitted_online) :
                                    getString(R.string.form_submitted_offline);
                            UtilityFunctions.showLongSnackbar(parent, snackbarMessage);
                        }));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutListener != null && !logoutListener.isDisposed()) {
            AndroidNetworking.cancel(Constants.LOGOUT_CALLS);
            logoutListener.dispose();
        }
        if (formSentDisposable != null && !formSentDisposable.isDisposed()) {
            formSentDisposable.dispose();
        }
        homePresenter.onDetach();
        unbinder.unbind();
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.samagra_shiksha);
        setSupportActionBar(toolbar);
       toolbar.setNavigationContentDescription("Hello");
    }

    @Override
    public void customizeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(this::initAndShowPopupMenu);
    }

    /**
     * Provides with a {@link AboutBundle} object that is used to further configure
     * the UI for {@link com.samagra.ancillaryscreens.screens.about.AboutActivity}
     */
    private AboutBundle provideAboutBundle() {
        return new AboutBundle(
                getActivityContext().getResources().getString(R.string.about_us),
                AppConstants.ABOUT_WEBSITE_LINK,
                AppConstants.ABOOUT_FORUM_LINK,
                R.drawable.saksham_haryana,
                R.string.samagra_shiksha,
                R.string.about_us_summary);
    }

    /**
     * Giving Control of the UI to XML file for better customization and easier changes
     */
    private void initAndShowPopupMenu(View v) {

        if (popupMenu == null) {
            popupMenu = new PopupMenu(HomeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.home_screen_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.change_lang:
                        if (HomeActivity.this.findViewById(R.id.fragment_container) != null) {
                            UpdateAppLanguageFragment firstFragment = UpdateAppLanguageFragment.newInstance(PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getActivityContext())
                                    .getString(Constants.APP_LANGUAGE_KEY, "en"), language -> {
                                SharedPreferences.Editor edit = PreferenceManager
                                        .getDefaultSharedPreferences(getActivityContext()).edit();
                                edit.putString(KEY_APP_LANGUAGE, language);
                                edit.putString(Constants.APP_LANGUAGE_KEY, language);
                                edit.apply();
                                relaunchHomeScreen();
                            });
                            HomeActivity.this.addFragment(R.id.fragment_container, HomeActivity.this.getSupportFragmentManager(), firstFragment, "UpdateAppLanguageFragment");
                            parentHome.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.about_us:
                        AncillaryScreensDriver.launchAboutActivity(HomeActivity.this, HomeActivity.this.provideAboutBundle());
                        break;
                    case R.id.tutorial_video:
                        homePresenter.onViewHelplineClicked();
                        break;
                    case R.id.profile:
                        ComponentManager.registerProfilePackage(new ProfileSectionInteractor(), ((MainApplication) (HomeActivity.this.getApplicationContext())),
                                AppConstants.BASE_API_URL,
                                AppConstants.APPLICATION_ID,
                                AppConstants.SEND_OTP_URL,
                                AppConstants.UPDATE_PASSWORD_URL,
                                HomeActivity.this.getApplicationContext().getResources().getString(R.string.fusionauth_api_key), homePresenter.fetchUserID());
                        IProfileContract initializer = ComponentManager.iProfileContract;
                        ArrayList<UserProfileElement> profileElements = homePresenter.getProfileConfig();
                        if (initializer != null) {
                            initializer.launchProfileActivity(HomeActivity.this.getActivityContext(), profileElements
                                    , HomeActivity.this.getActivityContext().getResources().getString(R.string.fusionauth_api_key));
                        }
                        break;
                    case R.id.logout:
                        if (homePresenter.isNetworkConnected()) {
                            if (logoutListener == null)
                                HomeActivity.this.initializeLogoutListener();
                            AncillaryScreensDriver.performLogout(HomeActivity.this, HomeActivity.this.getActivityContext().getResources().getString(R.string.fusionauth_api_key));
                        } else {
                            showSnackbar("It seems you are offline. Logout cannot happen in offline conditions.", 3000);
                        }
                        break;
                }
                return true;
            });
        }
        popupMenu.show();
    }


    private void initializeLogoutListener() {
        logoutListener = ((MainApplication) (getApplicationContext()))
                .getEventBus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Grove.d("Received event Logout");
                    if (o instanceof ExchangeObject.EventExchangeObject) {
                        ExchangeObject.EventExchangeObject eventExchangeObject = (ExchangeObject.EventExchangeObject) o;
                        if (eventExchangeObject.to == Modules.MAIN_APP && eventExchangeObject.from == Modules.ANCILLARY_SCREENS) {
                            if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_COMPLETED) {
                                hideLoading();
                                logoutListener.dispose();
                            } else if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_INITIATED) {
                                showLoading(getString(R.string.logging_out_message));
                            }
                        }
                    }
                }, Grove::e);
    }

    @Override
    public void showLoading(String message) {
        hideLoading();
        if (messageView == null) {
            messageView = UtilityFunctions.getSnackbarWithProgressIndicator(findViewById(android.R.id.content), getApplicationContext(), message);
        }
        messageView.setText(message);
        messageView.show();
    }

    @Override
    public void hideLoading() {
        if (messageView != null && messageView.isShownOrQueued())
            messageView.dismiss();
    }

    @Override
    public void launchSearchModule() {
        CascadingModuleDriver.init((MainApplication) getApplicationContext(), AppConstants.FILE_PATH, AppConstants.ROOT);
        CascadingModuleDriver.launchSearchView(getActivityContext(), AppConstants.ROOT + "/saksham_data_json.json", this);
    }

    @Override
    public void updateLocale(String language) {
        new LocaleHelper().updateLocale(getActivityContext(), language);

    }

    @Override
    public void displayHomeWelcomeText(String userName) {
        welcomeText.setText(String.format("%s  %s", getActivityContext().getResources().getString(R.string.welcome), userName));
    }

    @Override
    public void showNoInternetMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_download_error_no_internet_connection), Snackbar.LENGTH_LONG);
    }

    @Override
    public void showDownloadFailureMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_download_error), Snackbar.LENGTH_LONG);
    }

    @Override
    public void showFailureDownloadMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_list_download_error), Snackbar.LENGTH_LONG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE) {
            Grove.d("Received result from Search Activity...");
            String selectedDistrict = data.getStringExtra("selectedDistrict");
            String selectedBlock = data.getStringExtra("selectedBlock");
            String selectedSchool = data.getStringExtra("selectedSchool");
            InstitutionInfo institutionInfo = new InstitutionInfo(selectedDistrict, selectedBlock, selectedSchool);
            Grove.d("Selected District Name is >> " + selectedDistrict + "  Selected Block name is >> " + selectedBlock + "  Selected school name is >> " + selectedSchool);
            homePresenter.prefillData(institutionInfo);
            homePresenter.onFillFormsOptionClicked();
        }
    }
}