package com.psx.ancillaryscreens.screens.profile;

import androidx.annotation.NonNull;

import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.di.modules.CommonsActivityAbstractProviders;
import com.psx.ancillaryscreens.di.modules.CommonsActivityModule;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.psx.ancillaryscreens.screens.profile.ProfileElementViewHolders.ProfileElementHolder;

public class ProfilePresenter<V extends ProfileContract.View, I extends ProfileContract.Interactor> extends BasePresenter<V, I> implements ProfileContract.Presenter<V, I> {
    /**
     * These dependencies are provided by the {@link CommonsActivityModule} and
     * {@link CommonsActivityAbstractProviders} and are required by the Presenter
     * to carry out business logic tasks related to database access and/or netwrok access.
     *
     * @param mvpInteractor       - The interactor for the Activity. Must implement {@link MvpInteractor}
     * @param apiHelper           - It is the {@link BackendCallHelperImpl} singleton instance
     *                            required for performing N/W calls.
     * @param compositeDisposable - A {@link CompositeDisposable} object that keeps a track of all the API calls being
     *                            made in the current activity context. This object allows you to dispose all the calls
     */
    @Inject
    public ProfilePresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }

    /**
     * Initiates the SendOtpTask that sends an OTP on the user phone number.
     *
     * @param userPhone - The mobile number of the user on which the OTP needs to be send.
     */
    @Override
    public void startSendOTPTask(@NonNull String userPhone) {

    }

    /**
     * Updates the User's profile properties in {@link android.content.SharedPreferences}. The
     * updated properties are provided through the profileElementHolders parameter. This function
     * uses the {@link ProfileContract.Interactor} to access the {@link android.content.SharedPreferences}
     *
     * @param profileElementHolders - A list {@link ProfileElementHolder}s through which updated
     *                              values of a user profile can be accessed.
     */
    @Override
    public void updateUserProfile(ArrayList<ProfileElementHolder> profileElementHolders) {
        UserProfileElement userProfileElement;
        for (ProfileElementHolder elementHolder : profileElementHolders) {
            userProfileElement = elementHolder.getUserProfileElement();
            String contentKey = userProfileElement.getContent();
            String updatedValue = elementHolder.getUpdatedElementValue();
            getMvpInteractor().updateContentKeyInSharedPrefs(contentKey, updatedValue);
        }
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        return false;
    }

    @Override
    public boolean validateEmailAddress(String emailAddress) {
        return false;
    }

    /**
     * Fetches the latest value stored against a given key from the {@link android.content.SharedPreferences}.
     * This function uses the {@link ProfileContract.Interactor} to access the data from {@link android.content.SharedPreferences}
     *
     * @param key - The key against which the required content value is stored.
     */
    @Override
    public String getContentValueFromKey(String key) {
        return getMvpInteractor().getActualContentValue(key);
    }

}
