package com.samagra.ancillaryscreens.models;

public interface OnUserFound {

    void onSuccessUserFound(String s);

    void onFailureUserFound(Exception e);

}
