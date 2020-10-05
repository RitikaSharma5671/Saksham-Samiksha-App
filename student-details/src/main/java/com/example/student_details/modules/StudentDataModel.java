package com.example.student_details.modules;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.student_details.contracts.ApolloQueryResponseListener;
import com.example.student_details.models.realm.SchoolEmployeesInfo;
import com.example.student_details.models.realm.StudentInfo;
import com.hasura.model.FetchAttendanceByGradeSectionQuery;
import com.hasura.model.FetchAttendanceByGradeSectionStreamQuery;
import com.hasura.model.GetStudentsForSchoolQuery;
import com.hasura.model.SendAttendanceMutation;
import com.hasura.model.SendTeacherAttendanceMutation;
import com.hasura.model.UpdateStudentSectionMutation;
import com.hasura.model.type.Attendance_insert_input;
import com.hasura.model.type.Teacher_attendance_insert_input;
import com.samagra.grove.logging.Grove;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.OkHttpClient;

public class StudentDataModel {

    private static ApolloClient apolloClient = ApolloClient.builder()
            .serverUrl("http://167.71.227.241:5001/v1/graphql")
            .okHttpClient(new OkHttpClient.Builder()
                    .addInterceptor(new AuthorizationInterceptor())
                    .build()
            ).build();


    public void fetchStudentDataForSchool(String schoolCode, ApolloQueryResponseListener<GetStudentsForSchoolQuery.Data> apolloQueryResponseListener) {
        GetStudentsForSchoolQuery getStudentsForSchoolQuery = GetStudentsForSchoolQuery.builder().query_param(schoolCode).build();
        apolloClient.query(getStudentsForSchoolQuery).enqueue(new ApolloCall.Callback<GetStudentsForSchoolQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetStudentsForSchoolQuery.Data> response) {
                if(response.getErrors() == null && response.getData() != null) {
                    apolloQueryResponseListener.onResponseReceived(response);
                }else {
                    apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                apolloQueryResponseListener.onFailureReceived(e);
            }
        });
    }

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
                        Grove.e("Upload attendance failed for user " + userName + " with exception as " + e.getMessage());
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void uploadEmployeeAttendanceData(String date, String userName, List<SchoolEmployeesInfo> schoolEmployeesInfoList,
                                             ApolloQueryResponseListener<SendTeacherAttendanceMutation.Data> apolloQueryResponseListener) {
        List<Teacher_attendance_insert_input> teacher_attendance_insert_inputs = new ArrayList<>();
        for (SchoolEmployeesInfo schoolEmployeesInfo : schoolEmployeesInfoList) {
            Teacher_attendance_insert_input attendance_insert_input =
                    Teacher_attendance_insert_input.builder().taken_by(userName)
                            .employee_id(schoolEmployeesInfo.getEmployeeId())
                            .school_code(schoolEmployeesInfo.getSchoolCode())
                            .employee_name(schoolEmployeesInfo.getName())
                            .employee_designation(schoolEmployeesInfo.getDesignation())
                            .isPresent(schoolEmployeesInfo.isPresent())
                            .date(date)
                            .temperature(schoolEmployeesInfo.getTemp()).build();
            teacher_attendance_insert_inputs.add(attendance_insert_input);
        }
        SendTeacherAttendanceMutation sendTeacherAttendanceMutation = SendTeacherAttendanceMutation.builder().query_param(teacher_attendance_insert_inputs).build();
        apolloClient
                .mutate(sendTeacherAttendanceMutation)
                .enqueue(new ApolloCall.Callback<SendTeacherAttendanceMutation
                        .Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SendTeacherAttendanceMutation.Data> response) {
                        Grove.d("Attendance uploaded by " + userName + " for " + schoolEmployeesInfoList.size() + " employees with affected rows" +
                                response.getData().insert_teacher_attendance().affected_rows() + " and error fields as " + response.getErrors());
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
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

    public void updateStudentSection(String srn, String newSection,
                                     ApolloQueryResponseListener<UpdateStudentSectionMutation.Data> apolloQueryResponseListener) {
        UpdateStudentSectionMutation updateStudentSectionMutation = UpdateStudentSectionMutation.builder().srn(srn).changedSection(newSection).build();
        apolloClient
                .mutate(updateStudentSectionMutation)
                .enqueue(new ApolloCall.Callback<UpdateStudentSectionMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateStudentSectionMutation.Data> response) {
                        Grove.d("Section update request with response " +
                                response.getData().update_student().affected_rows() + " and error fields as " + response.getErrors() + " new section as " + response.getData().update_student());
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Grove.e("Upload attendance failed for user " + srn + " with exception as " + e.getMessage());
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void fetchAttendanceByGradeSection(String date, int grade, String section, String userName,
                                              ApolloQueryResponseListener<FetchAttendanceByGradeSectionQuery.Data> apolloQueryResponseListener) {
        FetchAttendanceByGradeSectionQuery fetchAttendanceByGradeSectionQuery = FetchAttendanceByGradeSectionQuery.builder().date(date).taken_by(userName).grade(grade)
                .section(section).build();
        apolloClient
                .query(fetchAttendanceByGradeSectionQuery)
                .enqueue(new ApolloCall.Callback<FetchAttendanceByGradeSectionQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FetchAttendanceByGradeSectionQuery.Data> response) {
//                        Grove.d("Section update request with response " +
//                                response.getData().attendance_aggregate().nodes().size() + " and error fields as " + response.getErrors() + " new section as " + response.getData().update_student());
                        if (response.getData() != null && response.getErrors() == null) {
                            apolloQueryResponseListener.onResponseReceived(response);
                        } else {
                            apolloQueryResponseListener.onFailureReceived(new ApolloException(response.getErrors().toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
//                        Grove.e("Upload attendance failed for user " + srn + " with exception as " + e.getMessage());
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

    public void fetchAttendanceByGradeSectionStream(String date, int grade, String section, String userName, String stream,
                                                    ApolloQueryResponseListener<FetchAttendanceByGradeSectionStreamQuery.Data> apolloQueryResponseListener) {
        FetchAttendanceByGradeSectionStreamQuery fetchAttendanceByGradeSectionStreamQuery = FetchAttendanceByGradeSectionStreamQuery.builder()
                .date(date).taken_by(userName).grade(grade)
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
//                        Grove.e("Upload attendance failed for user " + srn + " with exception as " + e.getMessage());
                        apolloQueryResponseListener.onFailureReceived(e);
                    }
                });
    }

}
