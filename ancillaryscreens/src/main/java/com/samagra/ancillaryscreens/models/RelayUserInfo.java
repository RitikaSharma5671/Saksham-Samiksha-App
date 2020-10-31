package com.samagra.ancillaryscreens.models;

import org.json.JSONObject;

import java.io.Serializable;

public class RelayUserInfo implements Serializable {
    int statusCode;
    JSONObject errorResponse;
    UserInformation successResponse;

    public int getStatusCode() {
        return statusCode;
    }

    public JSONObject getErrorResponse() {
        return errorResponse;
    }

    public UserInformation getSuccessResponse() {
        return successResponse;
    }
}
