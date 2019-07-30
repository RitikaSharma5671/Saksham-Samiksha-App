package com.psx.ancillaryscreens.data.network;

public final class BackendApiUrls {

    private static final String BASE_API_URL_SHIKSHA_SAATHI = "http://www.auth.samagra.io:9011";
    static final String AUTH_LOGIN_ENDPOINT = BASE_API_URL_SHIKSHA_SAATHI + "/api/login/";
    static final String USER_DETAILS_ENDPOINT = BASE_API_URL_SHIKSHA_SAATHI + "/api/user/{user_id}/";
}
