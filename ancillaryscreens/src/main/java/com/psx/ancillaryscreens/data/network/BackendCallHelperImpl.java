package com.psx.ancillaryscreens.data.network;

import com.google.gson.Gson;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import io.reactivex.Single;

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
}
