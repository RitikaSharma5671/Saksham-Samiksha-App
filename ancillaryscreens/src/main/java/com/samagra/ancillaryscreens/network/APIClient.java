package com.samagra.ancillaryscreens.network;

import android.content.Context;

import android.content.Context;
import android.text.TextUtils;


import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.BuildConfig;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public class APIClient {

    private volatile static Retrofit retrofit = null;
    private static APIInterface apiInterface = null;
    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";

    private synchronized static Retrofit getClient() {

        if (retrofit != null) {
            return retrofit;
        }

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        Interceptor timeoutInterceptor = chain -> {
            Request request = chain.request();

            int connectTimeout = chain.connectTimeoutMillis();
            int readTimeout = chain.readTimeoutMillis();
            int writeTimeout = chain.writeTimeoutMillis();

            String connectNew = request.header(CONNECT_TIMEOUT);
            String readNew = request.header(READ_TIMEOUT);
            String writeNew = request.header(WRITE_TIMEOUT);

            if (!TextUtils.isEmpty(connectNew)) {
                connectTimeout = Integer.valueOf(connectNew);
            }
            if (!TextUtils.isEmpty(readNew)) {
                readTimeout = Integer.valueOf(readNew);
            }
            if (!TextUtils.isEmpty(writeNew)) {
                writeTimeout = Integer.valueOf(writeNew);
            }

            return chain
                    .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .proceed(request);
        };

        final OkHttpClient okHttpClient;

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build();

        if (BuildConfig.DEBUG) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .addInterceptor(timeoutInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .addInterceptor(timeoutInterceptor)
                    .build();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    public static APIInterface getAPIInterface() {
        if (apiInterface == null) {
            apiInterface = getClient().create(APIInterface.class);
        }
        return apiInterface;
    }

    public static String getBaseUrl() {
        return AncillaryScreensDriver.BASE_API_URL;
    }

}