package com.samagra.user_profile.passReset;

import android.os.AsyncTask;

import com.samagra.grove.logging.Grove;
import com.samagra.user_profile.ProfileSectionDriver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UpdatePasswordTask extends AsyncTask<String, Void, String> {

    private ActionListener listener;
    private String TAG = UpdatePasswordTask.class.getName();
    private boolean isSuccessful = false;

    UpdatePasswordTask(ActionListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String[] strings) {
        String serverURL = ProfileSectionDriver.RESET_PASSWORD_URL;
        String phoneNo = strings[0];
        String otp = strings[1];
        String password = strings[2];

        JSONObject requestJson = new JSONObject();
        try {
            // Add values to json
            requestJson.put("phoneNo", phoneNo);
            requestJson.put("otp", otp);
            requestJson.put("password", password);
            requestJson.put("applicationId", ProfileSectionDriver.applicationID);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, requestJson.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverURL + "change-password")
                    .post(body)
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
                        listener.onFailure(new Exception("Couldn't change password. Please contact admin."));
                        e.printStackTrace();
                    }
                }else {
                    listener.onFailure(new Exception("Couldn't change password. Please try again after some time."));
                }
            } else {
                listener.onFailure(new Exception("Couldn't change password. Please contact admin."));
            }
        } else listener.onSuccess();
    }
}
