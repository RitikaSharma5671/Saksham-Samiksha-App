package com.example.student_details.modules;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.student_details.contracts.ApolloQueryResponseListener;
import com.example.student_details.models.realm.SchoolEmployeesAttendanceData;
import com.example.student_details.models.realm.StudentInfo;
import com.hasura.model.FetchAttendanceByGradeSectionQuery;
import com.hasura.model.FetchAttendanceByGradeSectionStreamQuery;
import com.hasura.model.FetchTeacherAttendanceQuery;
import com.hasura.model.SendAttendanceMutation;
import com.hasura.model.SendSMUpdateDataMutation;
import com.hasura.model.SendTeacherAttendanceNewFormatMutation;
import com.hasura.model.SendUsageInfoMutation;
import com.hasura.model.UpdateShikshaMitraDetailsMutation;
import com.hasura.model.UpdateStudentSectionMutation;
import com.hasura.model.type.Attendance_insert_input;
import com.hasura.model.type.Sm_usage_tracking_insert_input;
import com.hasura.model.type.Teacher_attendance_updated_insert_input;
import com.hasura.model.type.TrackInstall_insert_input;
import com.samagra.grove.logging.Grove;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;

//import com.hasura.model.FetchTeacherAttendanceQuery;

public class StudentDataModel {

    private static String token;

    public StudentDataModel(String token){
        this.token = token;
        apolloClient  = ApolloClient.builder()
                .serverUrl("http://167.71.227.241:5001/v1/graphql")
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(new AuthorizationInterceptor(token))
                        .build()
                ).build();
    }
    private static ApolloClient apolloClient ;


    public void uploadAttendanceData(String date, String userName, List<StudentInfo> list,
                                     ApolloQueryResponseListener<SendAttendanceMutation.Data> apolloQueryResponseListener) {
        List<Attendance_insert_input> dfde = new ArrayList<>();
        for (StudentInfo studentInfo : list) {
            Attendance_insert_input attendance_insert_input =
                    Attendance_insert_input.builder().taken_by(userName).
                            student(studentInfo.getSrn()).isPresent(studentInfo.isPresent()).
                            date(date).
                            temperature(studentInfo.getTemp()).build();
            dfde.add(attendance_insert_input);
        }

        SendAttendanceMutation upvotePostMutation = SendAttendanceMutation.builder().query_param(dfde).build();
        apolloClient
                .mutate(upvotePostMutation)
                .enqueue(new ApolloCall.Callback<SendAttendanceMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SendAttendanceMutation.Data> response) {
                        Grove.d("Attendance uploaded by " + userName + " for " + dfde.size() + " students with affected rows" +
                                response.getData().insert_attendance().affected_rows() + " and error fields as " + response.getErrors());
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        String s =e!= null && e.getStackTrace() != null ? Arrays.toString(e.getStackTrace()) :"";
                        Grove.e("Upload student attendance failed for user " + userName + " with exception as " + e.getMessage() + " , with stack trace "+ s);
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void uploadEmployeeAttendanceData(String date, String userName, List<SchoolEmployeesAttendanceData> schoolEmployeesInfoList,
                                             ApolloQueryResponseListener<SendTeacherAttendanceNewFormatMutation.Data> apolloQueryResponseListener) {
        List<Teacher_attendance_updated_insert_input> teacher_attendance_insert_inputs = new ArrayList<>();
        for (SchoolEmployeesAttendanceData schoolEmployeesInfo : schoolEmployeesInfoList) {
            Teacher_attendance_updated_insert_input attendance_insert_input =
                    Teacher_attendance_updated_insert_input.builder().taken_by(userName)
                            .employee_id(schoolEmployeesInfo.getEmployeeId())
                            .employee_name(schoolEmployeesInfo.getName())
                            .school_code(schoolEmployeesInfo.getSchoolCode())
                            .employee_designation(schoolEmployeesInfo.getDesignation())
                            .isPresentInSchool(schoolEmployeesInfo.isPresent())
                            .attendance_status(schoolEmployeesInfo.getAttendanceStatus())
                            .other_reason((schoolEmployeesInfo.getOtherReason() == null || schoolEmployeesInfo.getOtherReason().equals("")) ?
                                    "-" : schoolEmployeesInfo.getOtherReason())
                            .date_of_attendance(date)
                            .temperature(schoolEmployeesInfo.getTemp()).build();
            teacher_attendance_insert_inputs.add(attendance_insert_input);
        }
        SendTeacherAttendanceNewFormatMutation sendTeacherAttendanceMutation = SendTeacherAttendanceNewFormatMutation.builder().query_param(teacher_attendance_insert_inputs).build();
        apolloClient
                .mutate(sendTeacherAttendanceMutation)
                .enqueue(new ApolloCall.Callback<SendTeacherAttendanceNewFormatMutation
                        .Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SendTeacherAttendanceNewFormatMutation.Data> response) {
                        if (response.getData() != null && response.getErrors() == null && response.getData().insert_teacher_attendance_updated() != null &&
                                response.getData().insert_teacher_attendance_updated() .affected_rows() >0) {
                            Grove.d("Attendance uploaded by " + userName + " for " + schoolEmployeesInfoList.size() + " employees with affected rows" +
                                    response.getData().insert_teacher_attendance_updated().affected_rows() + " and error fields as " + response.getErrors());

                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            Grove.e("Employee Attendance Upload Failed with failure >>> " + response.getErrors());
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Grove.e("Upload attendance failed for user " + userName + " with exception as " + e.getMessage());
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }


    public void updateShikshaMitraDetails(String srn, String shikshaMitrName,String shikshaMitrContact, String shikshaMitrRelation,
                                          String shikshaMitrAddress,
                                          ApolloQueryResponseListener<UpdateShikshaMitraDetailsMutation.Data> apolloQueryResponseListener) {
         UpdateShikshaMitraDetailsMutation    updateShikshaMitraDetailsMutation = UpdateShikshaMitraDetailsMutation.builder().srn(srn).
                updatedShikshaMitraName(shikshaMitrName).updatedShikshaMitraContact(shikshaMitrContact).
                updatedShikshaMitraRelation(shikshaMitrRelation).
                updatedShikshaMitraAddress(shikshaMitrAddress).build();
        apolloClient
                .mutate(updateShikshaMitraDetailsMutation)
                .enqueue(new ApolloCall.Callback<UpdateShikshaMitraDetailsMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateShikshaMitraDetailsMutation.Data> response) {
                        if (response.getData() != null && response.getErrors() == null && response.getData().update_student() != null ) {
                            Grove.d("Section update request with response " +
                                    response.getData().update_student().affected_rows() + " and error fields as " + response.getErrors() + " new section as " + response.getData().update_student());
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        String s =e!= null && e.getStackTrace() != null ? Arrays.toString(e.getStackTrace()) :"";
                        Grove.e("Update Shiksha Mitr Details failed for the student " + srn + " with exception as " + e.getMessage() + " and stack trace" + s);
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void updateStudentSection(String srn, String newSection,
                                     ApolloQueryResponseListener<UpdateStudentSectionMutation.Data> apolloQueryResponseListener) {
        UpdateStudentSectionMutation updateStudentSectionMutation = UpdateStudentSectionMutation.builder().srn(srn).changedSection(newSection).build();
        apolloClient
                .mutate(updateStudentSectionMutation)
                .enqueue(new ApolloCall.Callback<UpdateStudentSectionMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateStudentSectionMutation.Data> response) {
                        if (response.getData() != null && response.getErrors() == null) {
                            Grove.d("Section update request with response " +
                                    response.getData().update_student().affected_rows() + " and error fields as " + response.getErrors() + " new section as " + response.getData().update_student());
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        String s =e!= null && e.getStackTrace() != null ? Arrays.toString(e.getStackTrace()) :"";
                        Grove.e("Update Student Attendance " + srn + " with exception as " + e.getMessage() + " with stack trace " + s);
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void fetchAttendanceByGradeSection(String date, int grade, String section,
                                              ApolloQueryResponseListener<FetchAttendanceByGradeSectionQuery.Data> apolloQueryResponseListener) {
        FetchAttendanceByGradeSectionQuery fetchAttendanceByGradeSectionQuery = FetchAttendanceByGradeSectionQuery.builder().date(date).grade(grade)
                .section(section).build();
        apolloClient
                .query(fetchAttendanceByGradeSectionQuery)
                .enqueue(new ApolloCall.Callback<FetchAttendanceByGradeSectionQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FetchAttendanceByGradeSectionQuery.Data> response) {
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void fetchAttendanceByGradeSectionStream(String date, int grade, String section, String stream,
                                                    ApolloQueryResponseListener<FetchAttendanceByGradeSectionStreamQuery.Data> apolloQueryResponseListener) {
        FetchAttendanceByGradeSectionStreamQuery fetchAttendanceByGradeSectionStreamQuery = FetchAttendanceByGradeSectionStreamQuery.builder()
                .date(date).grade(grade)
                .stream(stream)
                .section(section).build();
        apolloClient
                .query(fetchAttendanceByGradeSectionStreamQuery)
                .enqueue(new ApolloCall.Callback<FetchAttendanceByGradeSectionStreamQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FetchAttendanceByGradeSectionStreamQuery.Data> response) {
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void fetchEmployeeAttendanceForSchool(String date, String schoolCode,
                                                 ApolloQueryResponseListener<FetchTeacherAttendanceQuery.Data> apolloQueryResponseListener) {
        FetchTeacherAttendanceQuery fetchTeacherAttendanceQuery = FetchTeacherAttendanceQuery.builder().date(date).build();
        apolloClient
                .query(fetchTeacherAttendanceQuery)
                .enqueue(new ApolloCall.Callback<FetchTeacherAttendanceQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FetchTeacherAttendanceQuery.Data> response) {
                        if (response.getData() != null && response.getErrors() == null) {
                            Grove.d("Section update request with response " +
                                    response.getData().teacher_attendance_updated().size());
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().get(0).getMessage()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        String s =e!= null && e.getStackTrace() != null ? Arrays.toString(e.getStackTrace()) :"";
                        Grove.e("Upload attendance failed for school " + schoolCode + " with exception as " + e.getMessage() + " with stack trace: "+ s);
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }


    public void updateShikshaMitrUsageInfo(String username, String srn, String schoolCode,
                                            String designation, String previousName, String previousContact,
                                            ApolloQueryResponseListener<SendSMUpdateDataMutation.Data> apolloQueryResponseListener) {
        List<Sm_usage_tracking_insert_input> trackInstall_insert_inputs = new ArrayList<>();
        Sm_usage_tracking_insert_input trackInstall_insert_input = Sm_usage_tracking_insert_input.builder().school_code(schoolCode).username(username).
                sm_previous_contact(previousContact).sm_previous_name(previousName).designation(designation).srn(srn).build();
        trackInstall_insert_inputs.add(trackInstall_insert_input);
        SendSMUpdateDataMutation sendSMUpdateDataMutation = SendSMUpdateDataMutation.builder().query_param(trackInstall_insert_inputs).build();
        apolloClient.mutate(sendSMUpdateDataMutation).enqueue(new ApolloCall.Callback<SendSMUpdateDataMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<SendSMUpdateDataMutation.Data> response) {
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
}
