package com.samagra.ancillaryscreens.data.network.model;

import org.json.JSONObject;

import java.io.Serializable;

public class RelayLoginResponse implements Serializable {

    int statusCode;
    JSONObject errorResponse;
    LoginResponse successResponse;

    public int getStatusCode() {
        return statusCode;
    }

    public JSONObject getErrorResponse() {
        return errorResponse;
    }

    public LoginResponse getSuccessResponse() {
        return successResponse;
    }
}
