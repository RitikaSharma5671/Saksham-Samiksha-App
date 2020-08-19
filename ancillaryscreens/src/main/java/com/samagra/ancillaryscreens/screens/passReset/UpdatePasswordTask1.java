package com.samagra.ancillaryscreens.screens.passReset;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.grove.logging.Grove;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdatePasswordTask1 extends AsyncTask<String, Void, String> {

    private ChangePasswordActionListener listener;
    private String TAG = UpdatePasswordTask1.class.getName();
    private boolean isSuccessful = false;

    public UpdatePasswordTask1(){
    }

    @Override
    protected String doInBackground(String[] strings) {
        String serverURL = "https://www.auth.saksham.staging.samagra.io/api/user/{user_id}/";
        String phoneNo = strings[0];
        serverURL = serverURL.replace("{user_id}", phoneNo);
        String otp = strings[1];
        String bodt = strings[2];

        try {
            JSONObject requestJson = new JSONObject(bodt);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, requestJson.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Authorization", otp)
                    .url(serverURL)
                    .put(body)
                    .build();

            Response response;
            try {
                response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    isSuccessful = true;
                    Grove.d(TAG, "Successful Response, for Password API, sending control back to user");
                    return response.body().string();
                }else{
                    isSuccessful = false;
                    return response.body().string();
                }
            } catch (IOException e) {
                Grove.e(e);
                e.printStackTrace();
                return e.getMessage();
            }
        } catch (JSONException e) {
            Grove.e(e);
            e.printStackTrace();
            return e.getMessage();
        }

    }

    protected void onPostExecute(String s) {
        if (!isSuccessful) {
            if (s != null) {
                Grove.e("API Response to update pwd failed with this error: " + s);
                if (s.contains("status")){
                    try {
                        JSONObject responseObject = new JSONObject(s);
                        listener.onFailure(new Exception(responseObject.getString("status")));
                    } catch (JSONException e) {
                        listener.onFailure(new Exception(s));
                        e.printStackTrace();
                    }
                }else {
                    listener.onFailure(new Exception(s));
                }
            } else {
                listener.onFailure(new Exception("Password could not be changed. Please contact admin."));
            }
        } else listener.onSuccess();
    }
}
