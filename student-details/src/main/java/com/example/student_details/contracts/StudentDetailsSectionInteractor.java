package com.example.student_details.contracts;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.androidnetworking.error.ANError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.student_details.Utilities;
import com.example.student_details.models.realm.SchoolEmployeesInfo;
import com.example.student_details.models.realm.StudentInfo;
import com.example.student_details.modules.AuthorizationInterceptor;
import com.example.student_details.network.BackendCallHelperImpl;
import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo;
import com.example.student_details.ui.teacher_attendance.data.Employees;
import com.hasura.model.GetStudentsForSchoolQuery;
import com.samagra.grove.logging.Grove;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import timber.log.Timber;

public class StudentDetailsSectionInteractor implements IStudentDetailsContract {
    @Override
    public void markTeacherAttendance(Context activityContext,
                                      int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, HHH.class);
        intent.putExtra("nameOfActivity", "markTeacherAttendance");
        activityContext.startActivity(intent);
    }

    @Override
    public void viewStudentData(Context activityContext, int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, HHH.class);
        intent.putExtra("nameOfActivity", "studentData");
        activityContext.startActivity(intent);
    }

    @Override
    public void loadSchoolDistrictData() {
    }

    @Override
    public void fetchSchoolInfo(String school_code, String school_name, EmployeeInfoListener employeeInfoListener) {
        Realm realm = Realm.getDefaultInstance();
        new CompositeDisposable().add(BackendCallHelperImpl.getInstance()
                .performLoginApiCall(school_code, school_name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupResponse -> {
                    if (groupResponse != null && groupResponse.getTotal() > 0) {
                        realm.beginTransaction();
                        if (realm.getSchema().contains("SchoolEmployeesInfo"))
                            realm.delete(SchoolEmployeesInfo.class);
                        for (Employees employeeData : groupResponse.getUserInformation()) {
                            if (!employeeData.getData().getRoleData().getDesignation().equals("School Head")) {
                                SchoolEmployeesInfo schoolEmployeesInfo = new SchoolEmployeesInfo(employeeData.getUsername(), employeeData.getData().getAccountName(),
                                        employeeData.getData().getPhone(), employeeData.getData().getRoleData().getDesignation(),
                                        employeeData.getData().getRoleData().getSchoolCode(),
                                        employeeData.getData().getRoleData().getSchoolName(),
                                        employeeData.getData().getRoleData().getDistrict());
                                realm.copyToRealmOrUpdate(schoolEmployeesInfo);
                            }
                        }
                        realm.commitTransaction();
                    }
                    List<SchoolEmployeesInfo> employees = realm.copyFromRealm(realm
                            .where(SchoolEmployeesInfo.class).findAll());
                    employeeInfoListener.onSuccess(employees);

                }, throwable -> {
                    if (throwable instanceof ANError) {
                    } else {
                    }

                    Grove.e("Login error " + throwable);
                }));
    }

    @Override
    public ArrayList<ArrayList<String>> buildJSONArrayForEmployees(List<SchoolEmployeesInfo> employeeInfos) {
        ArrayList<ArrayList<String>> jsonArray = new ArrayList<ArrayList<String>>();
        ArrayList<String> first = new ArrayList<>();
        first.add("list_name");
        first.add("name");
        first.add("label");
        first.add("district");
        jsonArray.add(first);
        if (employeeInfos.size() > 0) {
            for (SchoolEmployeesInfo schoolEmployeesInfo : employeeInfos) {
                ArrayList<String> jsonObject = new ArrayList<>();
                jsonObject.add("students");
                jsonObject.add(schoolEmployeesInfo.getEmployeeId() + "_" + Utilities.convert(schoolEmployeesInfo.getName()).replace(" ", "_"));
                jsonObject.add(Utilities.convert(schoolEmployeesInfo.getName()) + " (" + schoolEmployeesInfo.getEmployeeId() + ")");
                jsonObject.add(schoolEmployeesInfo.getDistrict());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    @Override
    public void removeRealsmDB() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.deleteAll();
        }catch (Exception illegalStateException) {
            Timber.d("Unable to delete the realm DB:--  %s", illegalStateException.getMessage());
        }
    }

    @Override
    public void markStudentAttendance(Context activityContext,
                                      int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, HHH.class);
        intent.putExtra("nameOfActivity", "markStudentAttendance");
        activityContext.startActivity(intent);
    }

    @Override
    public void launchStudentAttendanceView(Context activityContext) {
        Intent intent = new Intent(activityContext, HHH.class);
        intent.putExtra("nameOfActivity", "markStudentAttendance");
        activityContext.startActivity(intent);
    }

    @Override
    public void fetchStudentData(String code, ApolloQueryResponseListener<GetStudentsForSchoolQuery.Data> apolloQueryResponseListener) {
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("http://167.71.227.241:5001/v1/graphql")
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(new AuthorizationInterceptor())
                        .build()
                ).build();

        GetStudentsForSchoolQuery getStudentsForSchoolQuery = GetStudentsForSchoolQuery.builder().query_param(code).build();
        apolloClient.query(getStudentsForSchoolQuery).enqueue(new ApolloCall.Callback<GetStudentsForSchoolQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetStudentsForSchoolQuery.Data> response) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                if (realm.getSchema().contains("StudentInfo"))
                    realm.delete(StudentInfo.class);
                List<GetStudentsForSchoolQuery.Student> jj = response.getData().student();
                for (GetStudentsForSchoolQuery.Student student : jj) {
                    StudentInfo studentInfo = new StudentInfo(student.srn(), student.name(), student.grade(),
                            student.section(), student.stream(), student.fatherName(), student.motherName(),
                            student.fatherContactNumber(), student.school_code());
                    realm.copyToRealmOrUpdate(studentInfo);
                }
                realm.commitTransaction();
                apolloQueryResponseListener.onResponseReceived(response);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                apolloQueryResponseListener.onFailureReceived(e);
            }
        });
    }

    @Override
    public ArrayList<ArrayList<String>> buildJSONArray() {
        ArrayList<ArrayList<String>> jsonArray = new ArrayList<ArrayList<String>>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<String> first = new ArrayList<>();
        first.add("list_name");
        first.add("name");
        first.add("label");
        first.add("section");
        first.add("grade");
        jsonArray.add(first);

        List<StudentInfo> studentInfoArrayList = realm.copyFromRealm(realm
                .where(StudentInfo.class).findAll());
        if (studentInfoArrayList != null && studentInfoArrayList.size() > 0) {
            for (StudentInfo studentInfo : studentInfoArrayList) {
                ArrayList<String> jsonObject = new ArrayList<>();
                jsonObject.add("students");
                jsonObject.add(studentInfo.getSrn() + "_" + Utilities.convert(studentInfo.getName()).replace(" ", "_"));
                jsonObject.add(Utilities.convert(studentInfo.getName()) + " (" + studentInfo.getSrn() + ")");
                jsonObject.add(studentInfo.getSection());
                jsonObject.add(String.valueOf(studentInfo.getGrade()));
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

}

