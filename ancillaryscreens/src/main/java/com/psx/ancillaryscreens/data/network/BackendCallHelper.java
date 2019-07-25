package com.psx.ancillaryscreens.data.network;

import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;

import io.reactivex.Single;

public interface BackendCallHelper {

    Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest);

}
