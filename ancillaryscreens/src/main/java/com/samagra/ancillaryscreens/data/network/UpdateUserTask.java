package com.samagra.ancillaryscreens.data.network;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.samagra.grove.logging.Grove;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings({"rawtypes", "unchecked", "ConstantConditions"})
public class UpdateUserTask extends AsyncTask<String, Void, LoginResponse> {

    String TAG = UpdateUserTask.class.getName();
    private UserUpdatedListener listener;
    private String user_id;
    private HashMap<String, String> userData;

    /**
     *  userEmail,
     *                         userName,
     *                         userPhone,
     *                         userJoiningDate,
     *                         userCategory,
     *                         userAccountName,
     *                         userId
     * @param listener
     * @param userID
     * @param hashMap
     */
    public UpdateUserTask(UserUpdatedListener listener, String userID, HashMap<String, String> hashMap){
        this.listener = listener;
        this.user_id = userID;
        this.userData = hashMap;
    }

    public boolean testIfPhoneUnique(String phone, String userId){
        boolean isPhoneUnique;
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyObject = new JSONObject();
        JSONObject search = new JSONObject();
        try {
            JSONArray sortFields = new JSONArray();
            JSONObject sortFieldsBody = new JSONObject();
            sortFieldsBody.put("name", "email");
            sortFields.put(sortFieldsBody);
            search.put("queryString", "(data.phone: " + phone + ") AND (registrations.applicationId: " +
                    AncillaryScreensDriver.APPLICATION_ID + ")");
            search.put("sortFields", sortFields);
            bodyObject.put("search", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(bodyObject.toString(),mediaType);
        Request request = new Request.Builder()
                .url(BackendApiUrls.USER_SEARCH_ENDPOINT)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", AncillaryScreensDriver.API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                LinkedTreeMap responseData = new Gson().fromJson(response.body().string(), LinkedTreeMap.class);
                JsonObject jsonObject = new Gson().toJsonTree(responseData).getAsJsonObject();
                int totalUsers = (int) Double.parseDouble(jsonObject.get("total").getAsJsonPrimitive().toString());
                if(totalUsers == 0) {
                    isPhoneUnique = true;
                }else if(totalUsers == 1){
                    JsonObject firstUser = (JsonObject) jsonObject.get("users").getAsJsonArray().get(0);
                    isPhoneUnique = firstUser.getAsJsonPrimitive("id").getAsString().equals(userId);
                }
                else {
                    isPhoneUnique = false;
                }
            }else isPhoneUnique = true;
        } catch (IOException e) {
            e.printStackTrace();
            isPhoneUnique = true;
        }
        return isPhoneUnique;
    }

    @Override
    protected LoginResponse doInBackground(String... strings) {
        boolean isPhoneUnique = testIfPhoneUnique(userData.get("phone"), user_id);
        if (!isPhoneUnique) {
            listener.onFailure("Multiple Users");
            return null;
        }
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = user_id;
        OkHttpClient client = new OkHttpClient();
        String authURL = BackendApiUrls.USER_DETAILS_ENDPOINT;
        authURL = authURL.replace("{user_id}", userId);
        String apiKey = AncillaryScreensDriver.API_KEY;
        Request request = new Request.Builder()
                .url(authURL)
                .get()
                .addHeader("Authorization", apiKey)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            Grove.e(TAG, "Response");
            if(response.isSuccessful()){
                Gson gsonObj = new Gson();
                LinkedTreeMap responseData = new Gson().fromJson(response.body().string(), LinkedTreeMap.class);
                JsonObject jsonObject = new Gson().toJsonTree(responseData).getAsJsonObject();
                Grove.e(TAG, "User1" + jsonObject.get("user"));
                JsonObject user = jsonObject.get("user").getAsJsonObject();
                if(userData.get("email") != null){user.addProperty("email", userData.get("email")); }
                user.addProperty("fullName",userData.get("name"));
                if(!userData.get("phone").equals(""))user.addProperty("mobilePhone", userData.get("phone"));
                JsonObject data = user.get("data").getAsJsonObject();
                if(!userData.get("phone").equals(""))data.addProperty("phone", userData.get("phone"));
                data.addProperty("accountName",userData.get("name"));
                user.add("data", data);
                responseData.put("user", user);
                String jsonStr = gsonObj.toJson(responseData);
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonStr);
                Request reqForUpdate = new Request.Builder()
                        .url(authURL + "/api/user/" + userId)
                        .put(body)
                        .addHeader("Authorization", apiKey)
                        .build();

                response = client.newCall(reqForUpdate).execute();
                Grove.e(TAG, "Response" + response.body().string());
                if(response.isSuccessful()){
                    try{
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        // Update shared prefs with the new data
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("user.email", strings[0]);
//                        editor.putString("user.fullName", strings[1]);
//                        editor.putString("user.mobilePhone", strings[2]);
//                        editor.putString("user.joiningDate", strings[3]);
//                        editor.putString("user.accountName", strings[5]);
//                        editor.apply();
//
//                        if(strings[5].equals("") || strings[2].equals("")){
//                            editor.putBoolean("isProfileComplete", false);
//                            editor.apply();
//                        }else{
//                            editor.putBoolean("isProfileComplete", true);
//                            editor.apply();
//                        }
                        listener.onSuccess(userData);
                    }catch (Exception e){
                            Grove.e(TAG, "Exception in updating user data", e);
                            listener.onFailure("Multiple Users");
                    }
                }else{
                    listener.onFailure("Multiple Users");
                }
            }else{
                listener.onFailure("Wrong Details entered in the profile details");
            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(e.getMessage());
        }
        return null;
    }
}
