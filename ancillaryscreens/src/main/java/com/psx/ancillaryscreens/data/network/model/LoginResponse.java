package com.psx.ancillaryscreens.data.network.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.Response;

public class LoginResponse {
    @Expose
    @SerializedName("token")
    private JsonPrimitive token;

    @Expose
    @SerializedName("user")
    private JsonObject user;

    @Expose
    @SerializedName("response")
    private Response response;

    LoginResponse(Response response){
        this.response = response;
    }

    public String getUserName(){
        return this.user.get("registrations")
                .getAsJsonArray().get(0).getAsJsonObject()
                .get("username").getAsString();
    }

    public JsonPrimitive getToken() {
        return token;
    }

    public void setToken(JsonPrimitive token) {
        this.token = token;
    }

    public JsonObject getUser() {
        return user;
    }

    public void setUser(JsonObject user) {
        this.user = user;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

