package com.example.student_details.contracts;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;

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
import com.example.student_details.ui.employee_aggregate.ViewEmployeeAttendance;
import com.example.student_details.ui.teacher_attendance.data.Employees;
import com.hasura.model.GetStudentsForSchoolQuery;
import com.hasura.model.SendUsageInfoMutation;
import com.hasura.model.type.TrackInstall_insert_input;
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

@SuppressWarnings("ConstantConditions")
public class StudentDetailsSectionInteractor implements IStudentDetailsContract {
    @Override
    public void markTeacherAttendance(Context activityContext,
                                      int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, SchoolModuleLauncherView.class);
        intent.putExtra("nameOfActivity", "markTeacherAttendance");
        activityContext.startActivity(intent);
    }

    @Override
    public void viewStudentData(Context activityContext, int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, SchoolModuleLauncherView.class);
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
                    Grove.e("Fetch Employee data error " + throwable.getMessage() + ", with error code: " + throwable.getCause());
                }));
    }

    @Override
    public ArrayList<ArrayList<String>> buildJSONArrayForEmployees() {
        Realm realm = Realm.getDefaultInstance();
        List<SchoolEmployeesInfo> employeeInfos = realm.copyFromRealm(realm
                .where(SchoolEmployeesInfo.class).findAll());
        ArrayList<ArrayList<String>> jsonArray = new ArrayList<>();
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
        } catch (Exception illegalStateException) {
            Timber.d("Unable to delete the realm DB:--  %s", illegalStateException.getMessage());
        }
    }

    @Override
    public void launchTeacherAttendanceView(Context activityContext) {
        Intent i = new Intent(activityContext, ViewEmployeeAttendance.class);
        activityContext.startActivity(i);
    }

    @Override
    public void updateUsageInfo(String username, String block, String schoolName, String schoolCode, String designation, String district, String misId, ApolloQueryResponseListener<SendUsageInfoMutation.Data> apolloQueryResponseListener) {
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("http://167.71.227.241:5001/v1/graphql")
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(new AuthorizationInterceptor())
                        .build()
                ).build();

        List<TrackInstall_insert_input> trackInstall_insert_inputs = new ArrayList<>();
        TrackInstall_insert_input trackInstall_insert_input = TrackInstall_insert_input.builder().schoolCode(schoolCode).district(district).block(block)
                .mis_id(username).schoolName(schoolName).designation(designation).username(username)
                .build();
        trackInstall_insert_inputs.add(trackInstall_insert_input);
        SendUsageInfoMutation sendUsageInfoMutation = SendUsageInfoMutation.builder().query_param(trackInstall_insert_inputs).build();
        apolloClient.mutate(sendUsageInfoMutation).enqueue(new ApolloCall.Callback<SendUsageInfoMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<SendUsageInfoMutation.Data> response) {
                if (response.getData() != null && response.getErrors() == null){
                    apolloQueryResponseListener.onResponseReceived(response);
                } else {
                    apolloQueryResponseListener.onResponseReceived(null);
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                apolloQueryResponseListener.onFailureReceived(e);
            }
        });
    }

    @Override
    public void markStudentAttendance(Context activityContext,
                                      int fragment_container, FragmentManager supportFragmentManager) {
        Intent intent = new Intent(activityContext, SchoolModuleLauncherView.class);
        intent.putExtra("nameOfActivity", "markStudentAttendance");
        activityContext.startActivity(intent);
    }

    @Override
    public void launchStudentAttendanceView(Context activityContext) {
        Intent intent = new Intent(activityContext, SchoolModuleLauncherView.class);
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

        GetStudentsForSchoolQuery getStudentsForSchoolQuery = GetStudentsForSchoolQuery.builder().school_code(code).build();
        apolloClient.query(getStudentsForSchoolQuery).enqueue(new ApolloCall.Callback<GetStudentsForSchoolQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetStudentsForSchoolQuery.Data> response) {
                Realm realm = Realm.getDefaultInstance();
                if (response.getData() != null && response.getErrors() == null && response.getData().student() != null &&
                        response.getData().student().size() > 0) {
                    List<GetStudentsForSchoolQuery.Student> jj = response.getData().student();
                    realm.beginTransaction();
                    if (realm.getSchema().contains("StudentInfo"))
                        realm.delete(StudentInfo.class);
                    for (GetStudentsForSchoolQuery.Student student : jj) {
                        StudentInfo studentInfo = new StudentInfo(student.srn(), student.name(), student.grade(),
                                student.section(), student.stream(), student.fatherName(), student.motherName(),
                                student.fatherContactNumber(), student.school_code());
                        realm.copyToRealmOrUpdate(studentInfo);
                    }
                    realm.commitTransaction();
                    apolloQueryResponseListener.onResponseReceived(response);
                } else {
                    apolloQueryResponseListener.onResponseReceived(null);
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                apolloQueryResponseListener.onFailureReceived(e);
            }
        });
    }

    @Override
    public ArrayList<ArrayList<String>> buildJSONArray() {
        ArrayList<ArrayList<String>> jsonArray = new ArrayList<>();
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