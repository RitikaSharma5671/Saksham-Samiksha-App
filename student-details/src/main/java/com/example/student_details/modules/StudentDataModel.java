package com.example.student_details.modules;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.hasura.model.AddNewStudentsMutation;
import com.hasura.model.GetStudentsForSchoolQuery;
import com.hasura.model.type.Student_insert_input;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import okhttp3.OkHttpClient;

public class StudentDataModel {



    public void fvf() {
        ApolloClient apolloClient =  ApolloClient.builder()
                .serverUrl("http://167.71.227.241:5001/v1/graphql")
                .okHttpClient(new OkHttpClient.Builder()
                .addInterceptor(new AuthorizationInterceptor())
                        .build()
                ).build();
        Student_insert_input student_insert_input = Student_insert_input.builder().class_(1).section("A").school_code("1").name("Sapna1").fatherName("Ram Kishore").srn("1219191933").build();
        List<Student_insert_input> dfde = new ArrayList<>();
        dfde.add(student_insert_input);


        AddNewStudentsMutation upvotePostMutation = AddNewStudentsMutation.builder().query_param(dfde).build();
        apolloClient
                .mutate(upvotePostMutation)
                .enqueue(new ApolloCall.Callback<AddNewStudentsMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<AddNewStudentsMutation.Data> response) {
                        Log.d("vgr bgr bgr" , response.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("vgr bgr bgr" , e.getMessage());
                    }
                });
    }
// Then enqueue your query


}
