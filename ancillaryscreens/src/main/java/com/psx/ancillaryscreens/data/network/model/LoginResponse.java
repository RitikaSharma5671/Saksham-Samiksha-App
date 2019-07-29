package com.psx.ancillaryscreens.data.network.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LoginResponse {

    public JsonPrimitive token;
    public JsonObject user;

    public String getUserName() {
        return this.user.get("registrations")
                .getAsJsonArray().get(0).getAsJsonObject()
                .get("username").getAsString();
    }

    @NonNull
    @Override
    public String toString() {
        return "token : " + token + " user " + user.toString();
    }
}

