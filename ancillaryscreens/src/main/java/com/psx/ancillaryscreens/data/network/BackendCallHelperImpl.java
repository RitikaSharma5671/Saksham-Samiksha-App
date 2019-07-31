package com.psx.ancillaryscreens.data.network;

import com.google.gson.Gson;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.psx.commons.Constants;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

public class BackendCallHelperImpl implements BackendCallHelper {

    private static BackendCallHelperImpl backendCallHelper = null;

    private BackendCallHelperImpl() {
        // This class Cannot be initialized directly
    }

    public static BackendCallHelperImpl getInstance() {
        if (backendCallHelper == null)
            backendCallHelper = new BackendCallHelperImpl();
        return backendCallHelper;
    }

    @Override
    public Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest) {
        return Rx2AndroidNetworking.post(BackendApiUrls.AUTH_LOGIN_ENDPOINT)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(loginRequest.getLoginRequestJSONObject())
                .build()
                .getJSONObjectSingle()
                .map(jsonObject -> {
                    LoginResponse loginResponse;
                    loginResponse = new Gson().fromJson(jsonObject.toString(), LoginResponse.class);
                    return loginResponse;
                });
    }

    @Override
    public Single<JSONObject> performGetUserDetailsApiCall(String userId, String apiKey) {
        return Rx2AndroidNetworking.get(BackendApiUrls.USER_DETAILS_ENDPOINT)
                .addPathParameter("user_id", userId)
                .addHeaders("Authorization", apiKey)
                .setTag(Constants.LOGOUT_CALLS)
                .build()
                .getJSONObjectSingle();
    }

    @Override
    public Single<JSONObject> performPutUserDetailsApiCall(String userId, String apiKey, JSONObject jsonObject) {
        return Rx2AndroidNetworking.put(BackendApiUrls.USER_DETAILS_ENDPOINT)
                .addPathParameter("user_id", userId)
                .addHeaders("Authorization", apiKey)
                .setTag(Constants.LOGOUT_CALLS)
                .addJSONObjectBody(jsonObject)
                .build()
                .getJSONObjectSingle();
    }
}
