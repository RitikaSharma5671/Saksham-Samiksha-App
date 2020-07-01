package com.samagra.ancillaryscreens.network.infra;

import android.os.AsyncTask;
import android.os.Process;

import com.google.gson.Gson;
import com.samagra.grove.logging.Grove;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_LESS_FAVORABLE;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class SamagraFetchDataTask extends AsyncTask<SamagraNetworkRequest, Integer, Object> {

    private SamagraDownloaderStatusCodes mStatus = SamagraDownloaderStatusCodes.NOT_STARTED; // setting mStatus, not started
    private Type resultObjectClass;  // this is the class type;

    /**
     * Constructor to initialize
     *
     * @param resultObjectClass - it is required to return the object result of this class type
     */
    public SamagraFetchDataTask(Type resultObjectClass) {
        this.resultObjectClass = resultObjectClass;
    }

    /**
     * Constructor to initialize
     *
     * @param resultObjectClass - it is required to return the object result of this class type
     */
    public SamagraFetchDataTask(SamagraNetworkRequest networkRequest, Type resultObjectClass) {
        this.resultObjectClass = resultObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInBackground(SamagraNetworkRequest... requests) {
        SamagraNetworkRequest request = requests[0];
        if (request.getRequestThreadPriority() == SamagraNetworkRequest.RequestThreadPriority.HIGH) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
        } else if (request.getRequestThreadPriority() == SamagraNetworkRequest.RequestThreadPriority.LOW) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_LESS_FAVORABLE);
        } else if (request.getRequestThreadPriority() == SamagraNetworkRequest.RequestThreadPriority.URGENT) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
        } else {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
        }
        long startTime = System.currentTimeMillis();
        try {
            String result = NetworkService.getHttpClient().fetchResponse(request);
            mStatus = SamagraDownloaderStatusCodes.SUCCESS;

            //Log.i("JSON response","request>>" + request.getBaseUrl() + " , " + request.getPostData() + "\nresponse >> " +  result);
            return getObjectFromJson(result);
        } catch (SocketTimeoutException e) {
            mStatus = SamagraDownloaderStatusCodes.REQUEST_TIMOUT;
        } catch (IOException e) {
            mStatus = SamagraDownloaderStatusCodes.ERROR;
            Grove.e("Exception occured while fetching url : " + request.getBaseUrl(),e);
        }
        return null;
    }

    /**
     * This method return mStatus value of type <code>GBDownloaderStatusCodes</code>
     *
     * @return - mStatus value of <code>GBDownloaderStatuscodes</code> type
     */
    public SamagraDownloaderStatusCodes getStatusCode() {
        return mStatus;
    }

    // this method convert json string into Java object of required Class type
    private Object getObjectFromJson(String jsonString) {
        Grove.d("GBFetchDataTask.getObjectFromJson() called");
        if (resultObjectClass == null) {
            // This block will return response data as it is (whether it is in JSON or other string
            // Hence user can get raw response string, if he pass responseObjectClass as null
            Grove.d("GBFetchDataTask.getObjectFromJson()---> resultObjectClass is null, return raw string as result");
            return jsonString;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString,resultObjectClass);
        } catch (Exception e) {
            mStatus = SamagraDownloaderStatusCodes.ERROR;
            Grove.e("GBFetchDataTask.getObjectFromJson() - Error occurred while parsing the JSON data :" + jsonString,e);
        }
        return null;
    }
}