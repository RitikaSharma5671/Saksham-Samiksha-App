package com.example.mysamagraapplication.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AppCacheInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder()
            .build()
    }
}