package com.samagra.ancillaryscreens.screens.passReset;

import android.os.AsyncTask;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.listeners.ActionListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendOTPTask extends AsyncTask<String, Void, String> {

    private ChangePasswordActionListener listener;
    private String TAG = SendOTPTask.class.getName();
    private boolean isSuccess;
    private String serverURL;

    public SendOTPTask(ChangePasswordActionListener listener, String serverURL){
        this.listener = listener;
        this.serverURL = serverURL;
    }

    @Override
    protected String doInBackground(String[] strings) {
        String phoneNo = strings[0];
        serverURL = serverURL + "send-OTP?phoneNo=" + phoneNo;
        OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverURL)
                    .get()
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    isSuccess = true;
                    response.body().close();
                    return response.body().toString();
                }else{
                    isSuccess = false;
                    return "Failure";
                }
            } catch (IOException e) {
                isSuccess = false;
                e.printStackTrace();
            }
            return null;
    }

    protected void onPostExecute(String s){
        if(isSuccess) {
            listener.onSuccess();
        }else{
            if(s != null)
                listener.onFailure(new Exception(s));
            else
                listener.onFailure(new Exception("Could not send OTP to the number."));
        }
    }
}
