package com.samagra.ancillaryscreens.screens.splash;

import com.samagra.ancillaryscreens.network.APIClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
class SplashModel {
    public static void validateJWTToken(String token, APIResponseListener apiResponseListener) {
        APIClient.getAPIInterface().getMerchantScreens(token).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                apiResponseListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                apiResponseListener.onError(APIResponseListener.UNKNOWN_ERROR, "Hello");
            }
        });
    }
}
