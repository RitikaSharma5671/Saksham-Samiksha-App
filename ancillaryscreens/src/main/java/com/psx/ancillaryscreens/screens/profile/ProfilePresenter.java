package com.psx.ancillaryscreens.screens.profile;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.psx.ancillaryscreens.R;
import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.di.modules.CommonsActivityAbstractProviders;
import com.psx.ancillaryscreens.di.modules.CommonsActivityModule;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
    public void updateUserProfileLocally(ArrayList<ProfileElementHolder> profileElementHolders) {
        UserProfileElement userProfileElement;
        for (ProfileElementHolder elementHolder : profileElementHolders) {
            userProfileElement = elementHolder.getUserProfileElement();
            String contentKey = userProfileElement.getContent();
            String updatedValue = elementHolder.getUpdatedElementValue();
            getMvpInteractor().updateContentKeyInSharedPrefs(contentKey, updatedValue);
        }
    }

    /**
     * Updates the User's profile properties at remote using FusionAuth APIs. The updated properties
     * are provided through the profileElementHolders parameter. This function first make an API
     * call to get the User's current details from remote using {@link BackendCallHelper} and then
     * modify the response to reflect updations and send the updated things back at remote.
     *
     * @param profileElementHolders - A list {@link ProfileElementHolder}s through which updated
     *                              values of a user profile can be accessed.
     */
    @Override
    public void updateUserProfileAtRemote(ArrayList<ProfileElementHolder> profileElementHolders) {
        String apiKey = getMvpView().getActivityContext().getResources().getString(R.string.fusionauth_api_key);
        String userId = getMvpInteractor().getCurrentUserId();
        // TODO : Implement
        getMvpView().showLoading("Updating User Profile...");
        getCompositeDisposable().add(getApiHelper()
                .performGetUserDetailsApiCall(userId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    // TODO : Implement
                    Timber.d("Suceess API. Response %s", jsonObject);
                    updateUserProfileLocally(profileElementHolders);
                    getMvpView().hideLoading();
                }, t -> {
                    Timber.e(t);
                    getMvpView().hideLoading();
                }));
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("[6-9][0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        return (m.find() && m.group().equals(phoneNumber));
    }

    @Override
    public boolean validateEmailAddress(String emailAddress) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(emailAddress);
        return m.matches();
    }

    @Override
    public boolean validateUdpatedFields(ArrayList<ProfileElementHolder> profileElementHolders) {
        for (ProfileElementHolder profileElementHolder : profileElementHolders) {
            if (profileElementHolder.getUserProfileElement().getProfileElementContentType() == UserProfileElement.ProfileElementContentType.PHONE_NUMBER) {
                if (!validatePhoneNumber(profileElementHolder.getUpdatedElementValue())) {
                    Toast.makeText(getMvpView().getActivityContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
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

    @Override
    public void onDestroy() {
        getMvpView().hideLoading();
        getCompositeDisposable().dispose();
    }

}
