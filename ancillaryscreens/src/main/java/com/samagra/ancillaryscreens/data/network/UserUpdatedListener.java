package com.samagra.ancillaryscreens.data.network;

import java.util.HashMap;

public interface UserUpdatedListener {
    /**
     * The operation succeeded
     */
    void onSuccess(HashMap<String,String> hashMap);
    /**
     * The operation failed
     * @param exception which caused the failure
     */
    void onFailure(String exception);
}
