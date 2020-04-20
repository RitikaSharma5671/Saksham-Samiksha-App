package com.samagra.user_profile.data.network;

import com.samagra.user_profile.ProfileSectionDriver;

final class BackendApiUrls {
    static final String AUTH_LOGIN_ENDPOINT = ProfileSectionDriver.BASE_API_URL + "/api/login";
    static final String USER_DETAILS_ENDPOINT = ProfileSectionDriver.BASE_API_URL + "/api/user/{user_id}/";
    static final String USER_SEARCH_ENDPOINT = ProfileSectionDriver.BASE_API_URL + "/api/user/search";
}
