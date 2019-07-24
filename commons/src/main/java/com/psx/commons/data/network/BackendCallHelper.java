package com.psx.commons.data.network;

import com.psx.commons.data.network.model.LoginRequest;
import com.psx.commons.data.network.model.LoginResponse;

import io.reactivex.Single;

public interface BackendCallHelper {

    Single<LoginResponse> performLoginApiCall(LoginRequest loginRequest);

}
