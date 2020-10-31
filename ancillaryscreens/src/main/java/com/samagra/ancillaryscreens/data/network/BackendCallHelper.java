package com.samagra.ancillaryscreens.data.network;

import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Single;

/**
 * Interface containing all the API Calls performed by this module.
 * All calls to be implemented in a single implementation of this interface.
 *
 * @author Pranav Sharma
 * @see BackendCallHelperImpl
 */
public interface BackendCallHelper {

    Single<JSONObject> refreshToken( String refreshToken, String jwt);

    Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest);

    Single<JSONObject> validateToken(String jwt);

    Single<JSONObject> performUpdatePassword(String phoneNumber, String otp, String password);

    Single<JSONObject> performGetUserDetailsApiCall(String userId, String apiKey);

    Single<JSONObject> performPutUserDetailsApiCall(String userId, String apiKey, JSONObject jsonObject);

    Single<JSONObject> performLogoutApiCall(String token, String apiKey) throws JSONException;
}
