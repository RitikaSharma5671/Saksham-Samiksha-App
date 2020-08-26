package com.samagra.parent.helper;

import org.json.JSONObject;

import io.reactivex.Single;

public interface BackendNwHelper {
    Single<JSONObject> refreshToken(String apiKey, String refreshToken);

    Single<JSONObject> validateToken(String jwt);
}
