package com.example.student_details.ui.teacher_attendance;

import android.os.AsyncTask;

import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo;
import com.google.gson.Gson;
import com.samagra.grove.logging.Grove;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindEmployeesBySchoolCodeTask extends AsyncTask<String, Void, String> {

    private OnEmployeeNetworkResponseListener listener;
//    String BASE_URL = A"ncillaryScreensDriver.BASE_API_URL + "/api/user/search"";
    private boolean isSuccess = false;

    public FindEmployeesBySchoolCodeTask(OnEmployeeNetworkResponseListener listener) {
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String[] strings) {
        String serverURL = "BASE_URL";
        String schoolCode = strings[0];
        String schoolName = strings[1];
        String apiKey = strings[2];
        String applicationId = strings[3];
        String json = "{\n" +
                "    \"search\": {\n" +
                "        \"queryString\": \"(registrations.applicationId: " + applicationId +
                ") AND (data.roleData.schoolCode: " + schoolCode + ") AND (data.roleData.schoolName : " + schoolName + ")\",\n" +
                "        \"sortFields\": []\n" +
                "            }\n" +
                "}";
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
                String APIResponse = response.body().string();
                return APIResponse;
            } else {
                Grove.e("Response Failure received for Find Users Task with failure " + response.body().string());
                isSuccess = false;
                String jsonData = response.body().string();
                response.body().close();
                return jsonData;
            }
        } catch (IOException e) {
            Grove.e("OTP Network R/Q failed with IO Exception at Login Screen with Exception " + e.getMessage());
            isSuccess = false;
            e.printStackTrace();
            return e.getMessage();
        }
    }

    protected void onPostExecute(String response) {
        if (isSuccess) {
            EmployeeInfo employeeInfo = new Gson().fromJson(response, EmployeeInfo.class);
            listener.onSuccess(employeeInfo);
        } else {
            if (response != null)
                listener.onFailure(new Exception(response));
            else
                listener.onFailure(new Exception("Could not send OTP to the number."));
        }
    }
}