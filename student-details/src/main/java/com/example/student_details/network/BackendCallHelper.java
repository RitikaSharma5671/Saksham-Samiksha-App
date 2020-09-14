package com.example.student_details.network;

import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo;

import org.json.JSONObject;

import io.reactivex.Single;

/**
 * Interface containing all the API Calls performed by this module.
 * All calls to be implemented in a single implementation of this interface.
 *
 * @author Umang Bhola
 * @see BackendCallHelperImpl
 */
public interface BackendCallHelper {
    Single<EmployeeInfo> performLoginApiCall(String schoolCode, String schoolName);
}
