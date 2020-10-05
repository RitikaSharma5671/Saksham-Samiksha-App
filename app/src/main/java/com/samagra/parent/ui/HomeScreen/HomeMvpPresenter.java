package com.samagra.parent.ui.HomeScreen;

import android.content.Context;

import com.samagra.commons.InstitutionInfo;
import com.samagra.parent.base.MvpPresenter;
import com.samagra.parent.di.PerActivity;
import com.samagra.ancillaryscreens.screens.profile.UserProfileElement;

import java.util.ArrayList;

/**
 * The Presenter 'contract' for the HomeScreen. The {@link HomePresenter} <b>must</b> implement this interface.
 * This interface exposes presenter methods to the view ({@link HomeActivity}) so that the business logic is defined
 * in the presenter, but can be called from the view.
 * This interface should be a type of {@link MvpPresenter}
 *
 * @author Pranav Sharma
 */
@PerActivity
public interface HomeMvpPresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends MvpPresenter<V, I> {

    void onViewHelplineClicked();

    void onViewSubmittedFormsOptionsClicked();

    void onSubmitFormsClicked();

    boolean isNetworkConnected();

    void onFillFormsOptionClicked();

    String getYoutubeAPIKey();

    String getTutorialVideoID();

    void applySettings();

    void checkForFormUpdates(boolean isStoragePermissionAvailable, Context context);

    ArrayList<UserProfileElement> getProfileConfig();

    void updateLanguageSettings();

    void resetODKData();

    void resetProgressVariables();

    void prefillData(InstitutionInfo institutionInfo);

    void searchmodule();

    void fetchWelcomeText();

    void fetchStudentData();

    ArrayList<String> fetchHomeItemList();

    boolean isProfileComplete();
}
