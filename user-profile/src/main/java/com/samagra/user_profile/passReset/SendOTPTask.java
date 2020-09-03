package com.samagra.user_profile.passReset;

import android.os.AsyncTask;

import com.samagra.grove.logging.Grove;
import com.samagra.user_profile.ProfileSectionDriver;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendOTPTask extends AsyncTask<String, Void, String> {

    private ChangePasswordActionListener listener;
    private String TAG = SendOTPTask.class.getName();
    private boolean isPhoneUnique;
    private boolean isSuccess = false;

    public SendOTPTask(ChangePasswordActionListener listener){
        this.listener = listener;
    }

    private boolean testIfPhoneUnique(){
        return true;
    }

    @Override
    protected String doInBackground(String[] strings) {
        isPhoneUnique = testIfPhoneUnique();
        if (!isPhoneUnique) {
            Grove.e(new Exception("Multiple users with the same phone number found. Contact Admin."));
            listener.onFailure(new Exception("Multiple users with the same phone number found. Contact Admin."));
            return null;
        } else {
            String serverURL = ProfileSectionDriver.SEND_OTP_URL;
            String phoneNo = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverURL + "send-OTP?phoneNo=" + phoneNo)
                    .get()
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    isSuccess = true;
                    response.body().close();
                    return response.body().toString();
                } else {
                    Grove.e("Response Failure received for Send OTP Task with failure " + response.body().string());
                    isSuccess = false;
                    String jsonData = response.body().string();
                    response.body().close();
                    return jsonData;
                }
            } catch (IOException e) {
                Grove.e("OTP Network R/Q failed with IO Exception at Login Screen with Execption " + e.getMessage());
                isSuccess = false;
                e.printStackTrace();
            }
            return null;
        }
    }


    protected void onPostExecute(String s) {
        if (isSuccess) {
            listener.onSuccess();
        } else {
            if (s != null)
                listener.onFailure(new Exception(s));
            else
                listener.onFailure(new Exception("Could not send OTP to the number."));
        }
    }
}