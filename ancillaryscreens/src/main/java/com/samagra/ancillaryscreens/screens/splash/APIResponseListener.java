package com.samagra.ancillaryscreens.screens.splash;

import org.json.JSONObject;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public interface APIResponseListener {

    int UNKNOWN_ERROR = 120;


    void onSuccess(JSONObject t);

    void onError(int reqId, String msg);
}