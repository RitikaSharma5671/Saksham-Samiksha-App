package com.samagra.ancillaryscreens.screens.change_password;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.data.network.BackendApiUrls;
import com.samagra.ancillaryscreens.models.OnUserFound;
import com.samagra.ancillaryscreens.models.UserInformation;
import com.samagra.grove.logging.Grove;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindUserByPhoneTask extends AsyncTask<String, Void, String> {

    private OnUserFound listener;
    String BASE_URL = BackendApiUrls.USER_SEARCH_ENDPOINT;
    private boolean isSuccess = false;

    public FindUserByPhoneTask(OnUserFound listener) {
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String[] strings) {
        String serverURL = BASE_URL;
        String phoneNo = strings[0];
        String apiKey = strings[1];
//
        String json = "{\n" +
                "    \"search\": {\n" +
                "        \"queryString\": \"(registrations.applicationId: " + AncillaryScreensDriver.APPLICATION_ID + ")AND(data.phone:" + phoneNo +
                ")\",\n" +
                "        \"sortFields\": [],\n" +
                "        \"numberOfResults\": 10,\n" +
                "        \"startRow\": 0\n" +
                "    }\n" +
                "}";
//        "{\"search\":{\"queryString\":\"(registrations.applicationId: 1ae074db-32f3-4714-a150-cc8a370eafd1) AND (data.phone: " + phoneNo + ")\",\"sortFields\":[{\"name\":\"email\"}]}}";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(json);
        } catch (Throwable t) {
            Grove.e("Could not parse malformed JSON");
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverURL)
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                isSuccess = true;
                String APIResponse  = response.body().string();
                UserInformation userDownloadData = new Gson().fromJson(APIResponse, UserInformation.class);
                return String.valueOf(userDownloadData.getTotal());
            } else {
                Grove.e("Response Failure received for Send OTP Task with failure " + response.body().string());
                isSuccess = false;
                String jsonData = response.body().string();
                response.body().close();
                return jsonData;
            }
        } catch (IOException e) {
            Grove.e("OTP Network R/Q failed with IO Exception at Login Screen with Exception " + e.getMessage());
            isSuccess = false;
            e.printStackTrace();
        }
        return null;

    }

    protected void onPostExecute(String s) {
        if (isSuccess) {
            listener.onSuccessUserFound(s);
        } else {
            if (s != null)
                listener.onFailureUserFound(new Exception(s));
            else
                listener.onFailureUserFound(new Exception("Could not send OTP to the number."));
        }
    }
}