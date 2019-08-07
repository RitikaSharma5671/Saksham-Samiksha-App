package com.psx.ancillaryscreens.screens.profile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.base.MvpPresenter;
import com.psx.ancillaryscreens.base.MvpView;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;

/**
 * The interface contract for Profile Screen. This interface contains the methods that the Model, View & Presenter
 * for Profile Screen <b>must</b> implement
 *
 * @author Pranav Sharma
 */
public interface ProfileContract {
    interface View extends MvpView {
        void initToolbar();

        /**
         * Initialises Calendar that is used to select User's Data of joining.
         */
        void initCalendar();

        /**
         * This method populates the fields displayed on the screen using multiple {@link UserProfileElement}
         * objects provided to {@link ProfileActivity} during its launch.
         *
         * @param userProfileElements - An {@link ArrayList} of {@link UserProfileElement} objects.
         *                            These list of objects represent individual elements of a User's
         *                            Profile.
         * @see com.psx.ancillaryscreens.AncillaryScreensDriver#launchProfileActivity(Context, ArrayList)
         */
        void initUserDetails(ArrayList<UserProfileElement> userProfileElements);

        void onProfileEditButtonClicked(View v);

        void onEditPasswordButtonClicked(View v);

        boolean validatePhoneNumber(String phoneNumber);
    }

    interface Interactor extends MvpInteractor {

    }

    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {
        /**
         * Initiates the SendOtpTask that sends an OTP on the user phone number.
         *
         * @param userPhone - The mobile number of the user on which the OTP needs to be send.
         */
        void startSendOTPTask(@NonNull String userPhone);
    }
}
