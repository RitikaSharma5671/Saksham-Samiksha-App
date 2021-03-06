package com.samagra.ancillaryscreens.screens.profile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.samagra.ancillaryscreens.base.MvpInteractor;
import com.samagra.ancillaryscreens.base.MvpPresenter;
import com.samagra.ancillaryscreens.base.MvpView;
import com.samagra.ancillaryscreens.data.network.BackendCallHelper;
import com.samagra.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;

import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.ProfileElementHolder;

/**
 * The interface contract for Profile Screen. This interface contains the methods that the Model, View & Presenter
 * for Profile Screen <b>must</b> implement.
 *
 * @author Pranav Sharma
 */
public interface ProfileContract {
    interface View extends MvpView {
        void initToolbar();

        /**
         * Initialises Calendar that is used to select User's Data of joining.
         * Not necessary to implement if there is no need for displaying date of joining.
         */
        default void initCalendar() {
        }

        /**
         * This method populates the fields displayed on the screen using multiple {@link UserProfileElement}
         * objects provided to {@link ProfileActivity} during its launch.
         *
         * @param userProfileElements - An {@link ArrayList} of {@link UserProfileElement} objects.
         *                            These list of objects represent individual elements of a User's
         *                            Profile.
         * @see com.samagra.ancillaryscreens.AncillaryScreensDriver#launchProfileActivity(Context, ArrayList)
         */
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
         * @param profileElementHolders - A list {@link ProfileElementHolder}s through which updated
         *                              values of a user profile can be accessed.
         */
        void updateUserProfileLocally(ArrayList<ProfileElementHolder> profileElementHolders);

        /**
         * Updates the User's profile properties at remote using FusionAuth APIs. The updated properties
         * are provided through the profileElementHolders parameter. This function first make an API
         * call to get the User's current details from remote using {@link BackendCallHelper} and then
         * modify the response to reflect updations and send the updated things back at remote.
         *
         * @param profileElementHolders - A list {@link ProfileElementHolder}s through which updated
         *                              values of a user profile can be accessed.
         */
        void updateUserProfileAtRemote(ArrayList<ProfileElementHolder> profileElementHolders);

        boolean validatePhoneNumber(String phoneNumber);

        boolean validateEmailAddress(String emailAddress);

        boolean validateUdpatedFields(ArrayList<ProfileElementHolder> profileElementHolders);

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
