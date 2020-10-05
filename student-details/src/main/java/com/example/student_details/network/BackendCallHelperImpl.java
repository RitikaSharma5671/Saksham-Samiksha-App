package com.example.student_details.network;

import androidx.annotation.NonNull;

import com.example.student_details.contracts.StudentDetailsComponentManager;
import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo;
import com.google.gson.Gson;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.samagra.commons.Constants;
import com.samagra.grove.logging.Grove;

import org.json.JSONObject;

import io.reactivex.Single;

/**
 * Solid implementation  of {@link BackendCallHelper} interface, constructs and executes the API calls
 * and returns an Observable for most functions so that the status of the calls can be observed.
 * The class maintains a singleton pattern allowing only a single instance of the class to exist at any given time.
 * This is done basically so that the class may be used outside the module without having to re-create an object.
 *
 * @author Pranav Sharma
 */
public class BackendCallHelperImpl implements BackendCallHelper {

    private static BackendCallHelperImpl backendCallHelper = null;

    private BackendCallHelperImpl() {
        // This class Cannot be initialized directly
    }

    private String API_USER_SEARCH_URL = StudentDetailsComponentManager.BASE_API_URL + "/api/user/search";
    /**
     * The method providing the singleton instance of this class. This methods automatically initiates the class
     * if it is null.
     */
    @NonNull
    public static BackendCallHelperImpl getInstance() {
        if (backendCallHelper == null)
            backendCallHelper = new BackendCallHelperImpl();
        return backendCallHelper;
    }

    @Override
    public Single<EmployeeInfo> performLoginApiCall(String schoolCode, String schoolName) {
        String json = "{\n" +
                "    \"search\": {\n" +
                "        \"numberOfResults\":500,\n"+
                "        \"queryString\": \"(registrations.applicationId: " + StudentDetailsComponentManager.APPLICATION_ID
                + ") AND (data.roleData.schoolCode: " + schoolCode + ") AND (data.roleData.schoolName : " + schoolName + ")\",\n" +
                "        \"sortFields\": [\n" +
                "             {\n" +
                "        \"name\": \"fullName\",\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "      ]\n" +
                "            }\n" +
                "}";
        JSONObject body = new JSONObject();
        try {
            body = new JSONObject(json);
        } catch (Throwable t) {
            Grove.e("Could not parse malformed JSON");
        }
        return Rx2AndroidNetworking.post(API_USER_SEARCH_URL)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", StudentDetailsComponentManager.API_KEY)
                .setTag(Constants.LOGOUT_CALLS)
                .addJSONObjectBody(body)
                .build()
                .getJSONObjectSingle()
                .map(jsonObject -> {
                    EmployeeInfo loginResponse;
                    loginResponse = new Gson().fromJson(jsonObject.toString(), EmployeeInfo.class);
                    return loginResponse;
                });
    }
}
