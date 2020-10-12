package com.example.student_details.contracts;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.example.student_details.models.realm.SchoolEmployeesInfo;
import com.hasura.model.GetStudentsForSchoolQuery;

import java.util.ArrayList;
import java.util.List;

public interface IStudentDetailsContract {
    void markTeacherAttendance(Context activityContext, int fragment_container, FragmentManager supportFragmentManager);

    void markStudentAttendance(Context activityContext, int fragment_container, FragmentManager supportFragmentManager);

    //    void fetchStudentData(String code);
    void fetchStudentData(String code, ApolloQueryResponseListener<GetStudentsForSchoolQuery.Data> apolloQueryResponseListener);

    void launchStudentAttendanceView(Context activityContext);

    ArrayList<ArrayList<String>> buildJSONArray();

    void viewStudentData(Context activityContext, int fragment_container, FragmentManager supportFragmentManager);

    void loadSchoolDistrictData();

    void fetchSchoolInfo(String school_code, String school_name, EmployeeInfoListener employeeInfoListener);

    ArrayList<ArrayList<String>> buildJSONArrayForEmployees();

    void removeRealsmDB();

    void launchTeacherAttendanceView(Context activityContext);
}
