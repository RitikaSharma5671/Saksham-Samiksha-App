package com.samagra.ancillaryscreens.network.infra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.reflect.Type;

public interface IDownloader {
    /**
     * This method execute Http/Https request in background and return the result, which could be directly used in Main (UI) Thread
     * @param url - url to be fetch
     * @param listener - callback where response will come
     * @param resultObjectClass - type of object in which the response to be serialize
     */
    void executeRequest (@NonNull String url, @Nullable IDownloadCompletedListener listener, @Nullable Type resultObjectClass);

    /**
     * This method execute Http/Https request in background and return the result, which could be directly used in Main (UI) Thread
     * @param request - GBNetworkRequest object
     * @param listener - callback where response will come
     * @param resultObjectClass - type of object in which the response to be serialize
     */
    void executeRequest (final SamagraNetworkRequest request, @Nullable final IDownloadCompletedListener listener, @Nullable Type resultObjectClass) ;

    /**
     * cancel request for particular url, callback will return with status <code>GBDownloaderStatusCode.REQUEST_CANCEL</code>
     * @param url
     */
    void cancel (String url);

    /**
     * Cancel network request which is still pending
     * @param request - instance of GBNetworkRequest
     */
    void cancelRequest (SamagraNetworkRequest request);
    /**
     * This method will cancel all requests for which callback is not recieved.
     */
    void cancelAll ();


}
