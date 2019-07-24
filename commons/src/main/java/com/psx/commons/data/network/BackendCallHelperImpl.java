package com.psx.commons.data.network;

import com.psx.commons.data.network.model.LoginRequest;
import com.psx.commons.data.network.model.LoginResponse;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class BackendCallHelperImpl implements BackendCallHelper{

    @Override
    public Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest) {
        return Rx2AndroidNetworking.post(BackendApiUrls.AUTH_LOGIN_ENDPOINT)
                .addHeaders("Content-Type","application/json")
                .addJSONObjectBody(loginRequest.getLoginRequestJSONObject())
                .build()
                .getObjectSingle(LoginResponse.class);
    }
}
