package com.psx.ancillaryscreens.data.network;

import com.psx.ancillaryscreens.AncillaryScreensDriver;

final class BackendApiUrls {
    static final String AUTH_LOGIN_ENDPOINT = AncillaryScreensDriver.BASE_URL + "/api/login";
    static final String USER_DETAILS_ENDPOINT = AncillaryScreensDriver.BASE_URL + "/api/user/{user_id}/";
}
