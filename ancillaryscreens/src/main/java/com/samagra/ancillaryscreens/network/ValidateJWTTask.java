package com.samagra.ancillaryscreens.network;

import android.os.AsyncTask;

import com.samagra.ancillaryscreens.data.network.BackendApiUrls;
import com.samagra.ancillaryscreens.screens.passReset.ChangePasswordActionListener;
import com.samagra.ancillaryscreens.screens.passReset.SendOTPTask;
import com.samagra.grove.logging.Grove;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public class ValidateJWTTask extends AsyncTask<String, Void, String> {

    private ChangePasswordActionListener listener;
    private String TAG = SendOTPTask.class.getName();
    private String token;

    public ValidateJWTTask(ChangePasswordActionListener listener, String currentToken) {
        this.listener = listener;
        this.token = currentToken;
    }


    @Override
    protected String doInBackground(String[] strings) {
        String serverURL = BackendApiUrls.VALIDATE_ENDPOINT;
        OkHttpClient client = new OkHttpClient();
        JSONObject body = new JSONObject();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestJson = RequestBody.create(JSON, body.toString());
        Request request = new Request.Builder()
                .addHeader("Authorization", "JWT " + token)
                .addHeader("Content-Type", "application/json")
                .url(serverURL)
                .post(requestJson)
                .build();
        Response response;

        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Grove.d(TAG, "Successful Response");
                return response.body().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(String s) {
        listener.onSuccess();

    }
}

