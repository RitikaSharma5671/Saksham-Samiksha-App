package com.samagra.ancillaryscreens.network;

import org.json.JSONObject;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public  interface APIInterface {
    @GET("/api/jwt/validate")
    @Headers("Content-Type: application/json")
    Call<JSONObject> getMerchantScreens(
            @Header("Authorization") String token);
}
