package com.samagra.ancillaryscreens.screens.passReset;

import android.os.AsyncTask;

import com.samagra.grove.logging.Grove;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ConstantConditions")
public class UpdatePasswordTask1 extends AsyncTask<String, Void, String> {

    private ChangePasswordActionListener listener;
    private String TAG = UpdatePasswordTask1.class.getName();
    private boolean isSuccessful = false;

    UpdatePasswordTask1(ChangePasswordActionListener listener){
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String[] strings) {
        String phoneNo = strings[0];
        String otp = strings[1];
        String serverURL = "http://142.93.208.135:8080/ams/verify-OTP/?phoneNo="+phoneNo + "&otp=" + otp;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverURL)
                .get()
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                if(response.body().string().contains("Success")) {
                    isSuccessful = true;
                    return response.body().string();
                }else if(response.body().string().contains("This OTP does not exis")) {
                    isSuccessful = false;
                    return response.body().string();
                }
                Grove.d(TAG, "Successful Response, for Password API, sending control back to user");
            }else{
                isSuccessful = false;
                return null;
            }
            return null;
        } catch (IOException e) {
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
