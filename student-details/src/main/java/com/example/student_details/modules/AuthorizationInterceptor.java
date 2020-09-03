package com.example.student_details.modules;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("x-hasura-admin-secret", "^zRr9gFrc%S4,}UugB")
            .build();

        return chain.proceed(request) ;
    }
}
