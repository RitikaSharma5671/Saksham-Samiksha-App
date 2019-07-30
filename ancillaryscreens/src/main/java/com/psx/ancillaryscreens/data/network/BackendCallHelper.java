package com.psx.ancillaryscreens.data.network;

import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;

import org.json.JSONObject;

import io.reactivex.Single;

public interface BackendCallHelper {

    Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest);

    Single<JSONObject> performGetUserDetailsApiCall(String userId, String apiKey);

    Single<JSONObject> performPutUserDetailsApiCall(String userId, String apiKey, JSONObject jsonObject);
}
