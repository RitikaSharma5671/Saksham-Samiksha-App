package com.samagra.ancillaryscreens.data.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.samagra.ancillaryscreens.data.network.model.RelayLoginResponse;
import com.samagra.commons.Constants;
import com.samagra.grove.logging.Grove;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Single;

/**
 * Solid implementation  of {@link BackendCallHelper} interface, constructs and executes the API calls
 * and returns an Observable for most functions so that the status of the calls can be observed.
 * The class maintains a singleton pattern allowing only a single instance of the class to exist at any given time.
 * This is done basically so that the class may be used outside the module without having to re-create an object.
 *
 * @author Pranav Sharma
 */
public class BackendCallHelperImpl implements BackendCallHelper {

    private static BackendCallHelperImpl backendCallHelper = null;

    private BackendCallHelperImpl() {
        // This class Cannot be initialized directly
    }

    /**
     * The method providing the singleton instance of this class. This methods automatically initiates the class
     * if it is null.
     */
    @NonNull
    public static BackendCallHelperImpl getInstance() {
        if (backendCallHelper == null)
            backendCallHelper = new BackendCallHelperImpl();
        return backendCallHelper;
    }

    @Override
    public Single<JSONObject> refreshToken(String refreshToken, String jwt) {
        JSONObject body = new JSONObject();
        try {
            body.put("refreshToken", refreshToken);
        } catch (Throwable t) {
            Grove.e("Could not parse malformed JSON");
        }
        return Rx2AndroidNetworking.post(BackendApiUrls.REFRESH_JWT_ENDPOINT)
                .addHeaders("Authorization", "Bearer " + jwt)
                .addHeaders("Content-Type", "application/json")
                .setTag(Constants.LOGOUT_CALLS)
                .addJSONObjectBody(body)
                .build()
                .getJSONObjectSingle();

    }

    @Override
    public Single<JSONObject> validateToken(String jwt){
        return Rx2AndroidNetworking.post(BackendApiUrls.VALIDATE_ENDPOINT)
                .addHeaders("Authorization", "Bearer " + jwt)
                .addHeaders("Content-Type", "application/json")
                .setTag(Constants.LOGOUT_CALLS)
                .build()
                .getJSONObjectSingle();
    }

    /**
     * This function executes the login api call using a {@link LoginRequest}. The API returns a {@link JSONObject}
     * which is first converted to a {@link LoginResponse} object and then used. Using the {@link JSONObject} directly
     * will cause an error.
     *
     * @param loginRequest - The {@link LoginRequest} object which contains relevant info required by the API. The info
     *                     from this object is first converted in {@link JSONObject} and then passed in the post request.
     * @return a {@link Single} object which receives the result of the API response and can be observed.
     * @see com.samagra.ancillaryscreens.screens.login.LoginPresenter#startAuthenticationTask(LoginRequest)
     * @see {https://fusionauth.io/docs/v1/tech/apis/login#authenticate-a-user}
     */
    @Override
    public Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest) {
        return Rx2AndroidNetworking.post(BackendApiUrls.AUTH_LOGIN_ENDPOINT)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(loginRequest.getLoginRequestJSONObject())
                .build()
                .getJSONObjectSingle()
                .map(jsonObject -> {
                    RelayLoginResponse relayLoginResponse;
                    relayLoginResponse = new Gson().fromJson(jsonObject.toString(), RelayLoginResponse.class);
                    return relayLoginResponse.getSuccessResponse();
                });
    }

    @Override
    public Single<JSONObject> performUpdatePassword(String phoneNumber, String otp, String password) {
        JSONObject requestJson = new JSONObject();
        try {
            // Add values to json
            requestJson.put("phoneNo", phoneNumber);
            requestJson.put("otp", otp);
            requestJson.put("password", password);
            requestJson.put("applicationId", AncillaryScreensDriver.APPLICATION_ID);
        }catch (Exception e) {

        }
        return Rx2AndroidNetworking.post(AncillaryScreensDriver.UPDATE_PASSWORD_URL+ "change-password")
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(requestJson)
                .build()
                .getJSONObjectSingle()
                .map(jsonObject -> {
                    return jsonObject;
                });
    }

    public Single<JSONObject> performLogoutApiCall(String token, String apiKey) throws JSONException {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("refreshToken", token);
        return Rx2AndroidNetworking.post(BackendApiUrls.AUTH_LOGOUT_ENDPOINT)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + apiKey)
                .addJSONObjectBody(jsonObject1)
                .build()
                .getJSONObjectSingle()
                .map(jsonObject -> {
                    return jsonObject;
                });
    }

    /**
     * This function performs a GET API call which retrieves all the details about the current user using fusion auth API.
     * This API call is also made during the logout process in {@link com.samagra.ancillaryscreens.AncillaryScreensDriver}.
     *
     * @param apiKey - The API key is used as authorization and passed in the headers.
     * @param userId - The unique id used to identify a user.
     * @return a {@link Single} object which receives the result of the API response and can be observed.
     * @see {https://fusionauth.io/docs/v1/tech/apis/users#retrieve-a-user}
     */
    @Override
    public Single<JSONObject> performGetUserDetailsApiCall(String userId, String apiKey) {
        Grove.d("performGetUserDetailsApiCall() method called...");
        return Rx2AndroidNetworking.get(BackendApiUrls.USER_DETAILS_ENDPOINT)
                .addPathParameter("user_id", userId)
                .addHeaders("Authorization", "Bearer " + apiKey)
                .setTag(Constants.LOGOUT_CALLS)
                .build()
                .getJSONObjectSingle();
    }

    /**
     * This function performs a PUT API call that updates a user object using fusion auth API.
     * This API call is also made during the logout process in {@link com.samagra.ancillaryscreens.AncillaryScreensDriver}.
     *
     * @param userId     - The unique id of the user that needs to be updated.
     * @param apiKey     - The API key is used as authorization and passed in the headers.
     * @param jsonObject - The updated user object that replaces the user with id userId at the backend.
     * @return a {@link Single} object which receives the result of the API response and can be observed.
     * @see {https://fusionauth.io/docs/v1/tech/apis/users#update-a-user}
     */
    @Override
    public Single<JSONObject> performPutUserDetailsApiCall(String userId, String apiKey, JSONObject jsonObject) {
        return Rx2AndroidNetworking.put(BackendApiUrls.USER_DETAILS_ENDPOINT)
                .addPathParameter("user_id", userId)
                .addHeaders("Authorization", "Bearer "+ apiKey)
                .setTag(Constants.LOGOUT_CALLS)
                .addJSONObjectBody(jsonObject)
                .build()
                .getJSONObjectSingle();
    }




}
