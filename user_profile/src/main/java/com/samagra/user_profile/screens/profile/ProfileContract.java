package com.samagra.user_profile.screens.profile;

import androidx.annotation.NonNull;

import com.samagra.user_profile.base.MvpInteractor;
import com.samagra.user_profile.base.MvpPresenter;
import com.samagra.user_profile.base.MvpView;
import com.samagra.user_profile.models.UserProfileElement;

import java.util.ArrayList;

/**
 * The interface contract for Profile Screen. This interface contains the methods that the Model, View & Presenter
 * for Profile Screen <b>must</b> implement.
 *
 * @author Pranav Sharma
 */
public interface ProfileContract {
    interface View extends MvpView {
        void initToolbar();

        void initUserDetails(ArrayList<UserProfileElement> userProfileElements);

        void onProfileEditButtonClicked(android.view.View v);

        void onEditPasswordButtonClicked(android.view.View v);

        void showLoading(String message);

        void hideLoading();
    }

    interface Interactor extends MvpInteractor {

        /**
         * This methods updates a single profile property which is represented in the
         * {@link android.content.SharedPreferences} by key with a given value.
         *
         * @param key   - The key with which the property is represented in the {@link android.content.SharedPreferences}
         * @param value - The value that needs to be stored against the key in the {@link android.content.SharedPreferences}
         */
        void updateContentKeyInSharedPrefs(String key, String value);

        String getActualContentValue(String key);

        String getCurrentUserId();
    }

    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {
        /**
         * Initiates the SendOtpTask that sends an OTP on the user phone number.
         *
         * @param userPhone - The mobile number of the user on which the OTP needs to be send.
         */
        void startSendOTPTask(@NonNull String userPhone);

        /**
         * Updates the User's profile properties in {@link android.content.SharedPreferences}. The
         * updated properties are provided through the profileElementHolders parameter. This function
         * uses the {@link Interactor} to access the {@link android.content.SharedPreferences}
         *
         */
        void updateUserProfileLocally(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders);

        void updateUserProfileAtRemote(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders);

        boolean validatePhoneNumber(String phoneNumber);

        boolean validateEmailAddress(String emailAddress);

        boolean validateUpdatedFields(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders);

        /**
         * Fetches the latest value stored against a given key from the {@link android.content.SharedPreferences}.
         * This function uses the {@link Interactor} to access the data from {@link android.content.SharedPreferences}
         *
         * @param key - The key against which the required content value is stored.
         */
        String getContentValueFromKey(String key);

        void onDestroy();
    }
}
