package com.samagra.ancillaryscreens.screens.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.samagra.ancillaryscreens.data.network.UpdateUserTask;
import com.samagra.ancillaryscreens.data.network.UserUpdatedListener;
import com.samagra.ancillaryscreens.screens.login.LoginActivity;
import com.samagra.commons.CommonUtilities;
import com.samagra.commons.Constants;
import com.samagra.commons.InstitutionInfo;
import com.samagra.grove.logging.Grove;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BasePresenter;
import com.samagra.ancillaryscreens.data.network.BackendCallHelper;

import org.jetbrains.annotations.Async;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.contracts.IFormManagementContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ProfilePresenter<V extends ProfileContract.View, I extends ProfileContract.Interactor> extends BasePresenter<V, I>
        implements ProfileContract.Presenter<V, I>, UserUpdatedListener {
    private ArrayList<ProfileElementViewHolders.ProfileElementHolder> viewHolder1;

    /**
     * These dependencies are provided by the {@link com.samagra.ancillaryscreens.di.modules.CommonsActivityModule} and
     * {@link com.samagra.ancillaryscreens.di.modules.CommonsActivityAbstractProviders} and are required by the Presenter
     * to carry out business logic tasks related to database access and/or netwrok access.
     *
     * @param mvpInteractor       - The interactor for the Activity. Must implement {@link com.samagra.ancillaryscreens.base.MvpInteractor}
     * @param apiHelper           - It is the {@link com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl} singleton instance
     *                            required for performing N/W calls.
     * @param compositeDisposable - A {@link CompositeDisposable} object that keeps a track of all the API calls being
     *                            made in the current activity context. This object allows you to dispose all the calls
     */
    @Inject
    public ProfilePresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, apiHelper, compositeDisposable, iFormManagementContract);
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
     * @param profileElementHolders - A list {@link ProfileElementViewHolders.ProfileElementHolder}s through which updated
     *                              values of a user profile can be accessed.
     */
    @Override
    public void updateUserProfileLocally(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders) {
        UserProfileElement userProfileElement;
        for (ProfileElementViewHolders.ProfileElementHolder elementHolder : profileElementHolders) {
            userProfileElement = elementHolder.getUserProfileElement();
            String contentKey = userProfileElement.getContent();
            String updatedValue = elementHolder.getUpdatedElementValue();
            AncillaryScreensDriver.sendEvent(contentKey, updatedValue);
            getMvpInteractor().updateContentKeyInSharedPrefs(contentKey, updatedValue);
        }
    }

    /**
     * Updates the User's profile properties at remote using FusionAuth APIs. The updated properties
     * are provided through the profileElementHolders parameter. This function first make an API
     * call to get the User's current details from remote using {@link BackendCallHelper} and then
     * modify the response to reflect updations and send the updated things back at remote.
     *
     * @param profileElementHolders - A list {@link ProfileElementViewHolders.ProfileElementHolder}s through which updated
     *                              values of a user profile can be accessed.
     */
    @Override
    public void updateUserProfileAtRemote(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders, String fusionAuthKey) {
        getMvpView().showLoading("Updating User Profile...");
        String apiKey = AncillaryScreensDriver.API_KEY;
        viewHolder1 = profileElementHolders;
        String userId = AncillaryScreensDriver.USER_ID;
        String oldPhone = getContentValueFromKey(profileElementHolders.get(1).getUserProfileElement().getContent());
        String updatedPhone = profileElementHolders.get(1).getUpdatedElementValue();
        String updatedAccountName = profileElementHolders.get(0).getUpdatedElementValue();
        String oldEmail = getContentValueFromKey(profileElementHolders.get(2).getUserProfileElement().getContent());
        String updatedEmail = profileElementHolders.get(2).getUpdatedElementValue();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("email", updatedEmail);
        hashMap.put("phone", updatedPhone);
        hashMap.put("name", updatedAccountName);
        new UpdateUserTask(this, AncillaryScreensDriver.USER_ID, hashMap)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//        Single<JSONObject> usersForPhone = getApiHelper().performSearchUserByPhoneCall(updatedPhone, apiKey);
////        Single<JSONObject> usersForEmail = getApiHelper().performSearchUserByEmailCall(updatedEmail, apiKey);
//        Single<JSONObject> updatedData = getApiHelper()
//                .performGetUserDetailsApiCall(userId, apiKey)
//                .flatMap(oldData -> {
//                    // Update the data with new fields
//                    Grove.d("Sending request to update the profile data");
//                    JSONObject user = oldData.getJSONObject("user");
//                    JSONObject internalData;
//                    if (user.has("data") && user.getJSONObject("data")!= null) {
//                        internalData = user.getJSONObject("data");
//                    } else {
//                        internalData = new JSONObject();
//                    }
//                    user.put("fullName",updatedAccountName);
//                    if(!updatedEmail.equals("") && validateEmailAddress(updatedEmail)){
//                        user.put("email", updatedEmail);
//                    }
//                    if(!updatedPhone.equals("") && validatePhoneNumber(updatedPhone)){
//                        user.put("mobilePhone", updatedPhone);
//                    }
//                    internalData.put("phone", updatedPhone);
//                    internalData.put("accountName", updatedAccountName);
//                    user.put("data", internalData);
//                    oldData.put("user", user);
//                    return getApiHelper().performPutUserDetailsApiCall(userId, apiKey, oldData);
//                });
//
//
//        if(oldPhone.equals(updatedPhone) && oldEmail.equals(updatedEmail)){
//            updateUserProfile(profileElementHolders, updatedData);
//        } else if (!oldPhone.equals(updatedPhone)) {
//            if (oldEmail.equals(updatedEmail)) {
//                updateUserProfile(profileElementHolders, updatedData);
//            } else {
//                getMvpView().hideLoading();
//                getMvpView().showSnackbar(getMvpView().getActivityContext().getResources().getString(R.string.phone_number_email_not_simultaneous), 2000);
//            }
//        } else if (!oldEmail.equals(updatedEmail)) {
//            if (oldPhone.equals(updatedPhone)) {
//                updateUserProfile(profileElementHolders, updatedData);
//            } else {
//                getMvpView().hideLoading();
//                getMvpView().showSnackbar(getMvpView().getActivityContext().getResources().getString(R.string.phone_number_email_not_simultaneous), 2000);
//            }
//        } else {
//            getMvpView().hideLoading();
//        }
    }
//
//    private void updateUserProfile(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders, Single<JSONObject> updatedData) {
//        getCompositeDisposable().add(updatedData.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(newData -> {
//                    Grove.d("Success API. Response %s", newData);
//                    updateUserProfileLocally(profileElementHolders);
//                    getMvpView().hideLoading();
//                    getMvpView().showSnackbar("User Details successfully updated.", 5000);
//                }, t -> {
//                    Grove.e(t);
//                    getMvpView().showSnackbar("Failed to update user profile.", 3000);
//                    getMvpView().hideLoading();
//                }));
//    }

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
    public boolean validateUpdatedFields(ArrayList<ProfileElementViewHolders.ProfileElementHolder> profileElementHolders) {
        int counter = 0;
        for (ProfileElementViewHolders.ProfileElementHolder profileElementHolder : profileElementHolders) {
            if (profileElementHolder.getUserProfileElement().getProfileElementContentType() == UserProfileElement.ProfileElementContentType.PHONE_NUMBER) {
                if (!validatePhoneNumber(profileElementHolder.getUpdatedElementValue())) {
                    Toast.makeText(getMvpView().getActivityContext(),
                            getMvpView().getActivityContext().getResources().getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (counter == 2) {
                if (!validateEmailAddress(profileElementHolder.getUpdatedElementValue())) {
                    Toast.makeText(getMvpView().getActivityContext(), getMvpView().getActivityContext().getResources().getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
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

    @Override
    public InstitutionInfo fetchSchoolDetails() {
        return getMvpInteractor().getPreferenceHelper().fetchSchoolInfo();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getMvpView()
                .getActivityContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isUserSchoolHead() {
        String designation = getMvpInteractor().getPreferenceHelper().fetchDesignation();
        return designation.contains("Head Master")
                || designation.contains("Head Master High School")
                || designation.contains("Head Teacher")
                || designation.equals("Principal")
                || designation.contains("DDO");
    }

    public boolean isTeacherAccount() {
        String designation = getMvpInteractor().getPreferenceHelper().fetchDesignation();
        return !designation.contains("DDO")&&(designation.contains("TGT") || designation.contains("Clerk")
                || designation.contains("Tabla Player") ||
                designation.contains("Vocational Instructor") ||
                designation.contains("Vocational PGT") ||
                designation.contains("Classical & Vernacular Teacher") ||
                designation.contains("JBT") || designation.contains("PGT"));
    }

    public boolean isSchoolAccount() {
        String designation = getMvpInteractor().getPreferenceHelper().fetchDesignation();
        return designation.contains("School Head");
    }

    public void performUpdateSchoolCode(Context activityContext, String apiKey, InstitutionInfo institutionInfo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
        String userId = sharedPreferences.getString("user.id", "");
        BackendCallHelperImpl.getInstance()
                .performGetUserDetailsApiCall(userId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Grove.d("OnSubscribe make Fetch User data call onSubscribe() called");
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        JSONObject removedFCMTokenObject = updateUserObject(jsonObject, institutionInfo);
                        Grove.d("Successfully fetched user data to update the school details");
                        putUpdatedUserDetailsObject(removedFCMTokenObject, activityContext, userId, apiKey);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null)
                            getMvpView().onErrorUpdateSchoolData();
                        Grove.e("onError() called while fetching user data to update the school details with error Exception: " + e.getMessage());
                    }
                });
    }

    private void putUpdatedUserDetailsObject(JSONObject jsonObjectToPut, Context activityContext, String userId, String apiKey) {
        BackendCallHelperImpl.getInstance()
                .performPutUserDetailsApiCall(userId, apiKey, jsonObjectToPut)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Grove.d("On Subscribe Put updated objects... putUpdatedUserDetailsObject() called ");
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            JSONObject roleData = jsonObject.getJSONObject("user").getJSONObject("data").getJSONObject("roleData");
                            InstitutionInfo institutionInfo = new InstitutionInfo(roleData.getString("district"), roleData.getString("block"),
                                    roleData.getString("schoolName"), Integer.parseInt(roleData.getString("schoolCode")));
                            if (getMvpView() != null)
                                getMvpView().onSuccessDone(institutionInfo);
                            getMvpInteractor().getPreferenceHelper().updateSchoolDetails(institutionInfo);
                            Grove.d("Successfully changed the school district details ");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null)
                            getMvpView().onErrorUpdateSchoolData();
                        Grove.e("onError for school district details called " + e.getMessage());
                    }
                });
    }

    private JSONObject updateUserObject(JSONObject jsonObject, InstitutionInfo institutionInfo) {
        Grove.d("Removing FCM Token from %s", jsonObject.toString());
        try {
            JSONObject user = jsonObject.getJSONObject("user");
            JSONObject data = user.getJSONObject("data");
            JSONObject roleData = data.getJSONObject("roleData");
            roleData.put("schoolName", institutionInfo.getSchoolName());
            roleData.put("schoolCode", String.valueOf(institutionInfo.getSchoolCode()));
            roleData.put("block", institutionInfo.getBlock());
            roleData.put("district", institutionInfo.getDistrict());
            data.put("roleData", roleData);
            user.put("data", data);
            jsonObject.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onSuccess(HashMap<String, String> hashMap) {
        updateUserProfileLocally(viewHolder1);
        getMvpView().hideLoading();
        getMvpView().showSnackbar("Success", 5000);
    }

    @Override
    public void onFailure(String exception) {
            Grove.e("Failed to update user profile with the exception:  " + exception);
            if(exception.equals("Multiple Users")) {
                getMvpView().hideLoading();
                getMvpView().showSnackbar("Multiple Users", 5000);
            }
            getMvpView().showSnackbar("Failed to update user profile.", 3000);
            getMvpView().hideLoading();
    }
}
