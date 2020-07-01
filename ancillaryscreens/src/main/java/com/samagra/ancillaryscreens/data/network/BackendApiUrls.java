package com.samagra.ancillaryscreens.data.network;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.commons.MainApplication;

/**
 * Class that contains all the API endpoints required by the Ancillary Screens.
 * Note : This class should only contain static final String variables indicating the API endpoints. {@link AncillaryScreensDriver#BASE_API_URL}
 * is provided in {@link AncillaryScreensDriver#init(MainApplication, String, String, String,String,String)}
 *
 * @author Pranav Sharma
 * @see AncillaryScreensDriver#init(MainApplication, String, String, String, String, String)
 */
public final class BackendApiUrls {
    public static final String ROLE_ENDPOINT = "http://luezoid.com:3390/relayer/api/user/3c40f18e-b433-41bd-ab7b-d1de442837e6";
    static final String AUTH_LOGIN_ENDPOINT = AncillaryScreensDriver.BASE_API_URL + "/api/login";
    static final String USER_DETAILS_ENDPOINT = AncillaryScreensDriver.BASE_API_URL + "/api/user/{user_id}/";
    static final String REFRESH_JWT_ENDPOINT = AncillaryScreensDriver.BASE_API_URL + "/api/jwt/refresh";
    public static final String VALIDATE_ENDPOINT = AncillaryScreensDriver.BASE_API_URL + "/api/jwt/validate";
    public static final String APP_ENDPOINT ="http://luezoid.com:3390/relayer/api/application/1ae074db-32f3-4714-a150-cc8a370eafd1";
}
