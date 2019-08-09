package com.psx.odktest.ui.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.google.android.material.snackbar.Snackbar;
import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.ancillaryscreens.models.UserProfileElement;
import com.psx.ancillaryscreens.models.AboutBundle;
import com.psx.commons.Constants;
import com.psx.commons.CustomEvents;
import com.psx.commons.ExchangeObject;
import com.psx.commons.MainApplication;
import com.psx.commons.Modules;
import com.psx.odktest.AppConstants;
import com.psx.odktest.R;
import com.psx.odktest.UtilityFunctions;
import com.psx.odktest.base.BaseActivity;

import org.odk.collect.android.ODKDriver;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * View part of the Home Screen. This class only handles the UI operations, all the business logic is simply
 * abstracted from this Activity. It <b>must</b> implement the {@link HomeMvpView} and extend the {@link BaseActivity}
 *
 * @author Pranav Sharma
 */
public class HomeActivity extends BaseActivity implements HomeMvpView, View.OnClickListener {

    @BindView(R.id.welcome_text)
    public TextView welcomeTextView;
    @BindView(R.id.helpline_button)
    public Button helplineButton;
    @BindView(R.id.submit_form)
    public LinearLayout submitFormLinearLayout;
    @BindView(R.id.inspect_school)
    public LinearLayout inspectSchoolLinearLayout;
    @BindView(R.id.view_filled_forms)
    public LinearLayout viewFilledFormsLinearLayout;
    @BindView(R.id.view_issues)
    public LinearLayout viewIssuesLinearLayout;

    private PopupMenu popupMenu;
    private Disposable logoutListener = null;
    private Snackbar progressSnackbar = null;

    private Unbinder unbinder;

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
        setupListeners();
        checkIntent();
        homePresenter.setWelcomeText();
        homePresenter.applySettings();
        homePresenter.downloadForms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    private void setupListeners() {
        helplineButton.setOnClickListener(this);
        submitFormLinearLayout.setOnClickListener(this);
        inspectSchoolLinearLayout.setOnClickListener(this);
        viewFilledFormsLinearLayout.setOnClickListener(this);
        viewIssuesLinearLayout.setOnClickListener(this);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("ShowSnackbar", false)) {
            if (homePresenter.isNetworkConnected())
                showSnackbar(getString(R.string.on_internet_saving_complete), Snackbar.LENGTH_SHORT);
            else
                showSnackbar(getString(R.string.no_internet_saving_complete), Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.helpline_button:
                homePresenter.onHelplineButtonClicked(v);
                break;
            case R.id.submit_form:
                homePresenter.onSubmitFormClicked(v);
                break;
            case R.id.inspect_school:
                homePresenter.onInspectSchoolClicked(v);
                break;
            case R.id.view_filled_forms:
                homePresenter.onMyVisitClicked(v);
                break;
            case R.id.view_issues:
                homePresenter.onViewIssuesClicked(v);
                break;
        }
    }

    @Override
    public void updateWelcomeText(String text) {
        welcomeTextView.setText(text);
    }

    @Override
    public void showLoading(String message) {
        hideLoading();
        if (progressSnackbar == null) {
            progressSnackbar = UtilityFunctions.getSnackbarWithProgressIndicator(findViewById(android.R.id.content), getApplicationContext(), message);
        }
        progressSnackbar.setText(message);
        progressSnackbar.show();
    }

    @Override
    public void hideLoading() {
        if (progressSnackbar != null && progressSnackbar.isShownOrQueued())
            progressSnackbar.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutListener != null && !logoutListener.isDisposed()) {
            AndroidNetworking.cancel(Constants.LOGOUT_CALLS);
            logoutListener.dispose();
        }
        homePresenter.onDetach();
        unbinder.unbind();
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use {@link ODKDriver}. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Shiksha Saathi");
        setSupportActionBar(toolbar);
    }

    public void customizeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(this::initAndShowPopupMenu);
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
                    case R.id.about_us:
                        AncillaryScreensDriver.launchAboutActivity(this, provideAboutBundle());
                        break;
                    case R.id.tutorial_video:
                        Toast.makeText(HomeActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.profile:
                        //TODO : Remove this dummy element. Add Valid elements from Firebase
                        ArrayList<UserProfileElement> userProfileElements = new ArrayList<>();
                        userProfileElements.add(new UserProfileElement("", "Name", "TEST", true, 0, UserProfileElement.ProfileElementContentType.TEXT, null));
                        userProfileElements.add(new UserProfileElement("", "Date Joined", "26 January, 2019", false, 1, UserProfileElement.ProfileElementContentType.DATE, null));
                        userProfileElements.add(new UserProfileElement("", "Official", "TEST2@gmail.com", false, 0, UserProfileElement.ProfileElementContentType.TEXT, null));
                        userProfileElements.add(new UserProfileElement("", "Contact Number - Please note this number will be used for sending OTP for password reset.", "9161986851", true, 0, UserProfileElement.ProfileElementContentType.PHONE_NUMBER, null));
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add("Category one");
                        strings.add("Category two");
                        strings.add("Category three");
                        userProfileElements.add(new UserProfileElement("", "Category", "CATEGORY 1", true, 1, UserProfileElement.ProfileElementContentType.SPINNER, strings));
                        AncillaryScreensDriver.launchProfileActivity(this, userProfileElements);
                        break;
                    case R.id.helpline:
                        Toast.makeText(HomeActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.important_links:
                        Toast.makeText(HomeActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logout:
                        if (logoutListener == null)
                            initializeLogoutListener();
                        AncillaryScreensDriver.performLogout(this);
                        break;
                }
                return true;
            });
        }
        popupMenu.show();
    }

    /**
     * Provides with a {@link AboutBundle} object that is used to further configure
     * the UI for {@link com.psx.ancillaryscreens.screens.about.AboutActivity}
     */
    private AboutBundle provideAboutBundle() {
        return new AboutBundle(
                "About Us",
                AppConstants.ABOUT_WEBSITE_LINK,
                AppConstants.ABOOUT_FORUM_LINK,
                R.drawable.ic_website_24dp,
                R.string.odk_website,
                R.string.odk_website_summary);
    }

    /**
     * This function subsribe to the {@link com.psx.commons.RxBus} to listen for the Logout related events
     * and update the UI accordingly. The events being subscribed to are {@link com.psx.commons.CustomEvents#LOGOUT_COMPLETED}
     * and {@link com.psx.commons.CustomEvents#LOGOUT_INITIATED}
     *
     * @see com.psx.commons.CustomEvents
     */
    @Override
    public void initializeLogoutListener() {
        logoutListener = ((MainApplication) (getApplicationContext()))
                .getEventBus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Timber.i("Received event Logout");
                    if (o instanceof ExchangeObject.EventExchangeObject) {
                        ExchangeObject.EventExchangeObject eventExchangeObject = (ExchangeObject.EventExchangeObject) o;
                        if (eventExchangeObject.to == Modules.MAIN_APP && eventExchangeObject.from == Modules.ANCILLARY_SCREENS) {
                            if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_COMPLETED) {
                                hideLoading();
                                logoutListener.dispose();
                            } else if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_INITIATED) {
                                showLoading("Logging you out...");
                            }
                        }
                    }
                }, Timber::e);
    }
}
