package com.samagra.ancillaryscreens.network.infra;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.samagra.grove.logging.Grove;

import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class SamagraAsyncDataDownloader implements IDownloader {

    //  Active Tasks which represents the task which are currently executing and not finished yet.
    private ConcurrentHashMap<SamagraNetworkRequest, AsyncTask> mActiveTasks; //TODO: this data structure will break

    public SamagraAsyncDataDownloader() {
        mActiveTasks = new ConcurrentHashMap<>();
    }


    /**
     * This method executes request using <code>AsyncTask</code> and return result in IDownloadCopletedListener callback
     *
     * @param url      - http url request
     * @param listener - callback (could be <code>null</code> to return the result (Http response string)
     */
    @Override
    public void executeRequest(@NonNull final String url, @Nullable final IDownloadCompletedListener listener, @Nullable Type resultObjectClass) {
        SamagraNetworkRequest request = new SamagraNetworkRequest();
        request.setType(SamagraHttpClient.GBRequestType.GET);
        request.setBaseUrl(url);

        executeRequest(request,listener,resultObjectClass);
    }


    /**
     * This method executes request using <code>AsyncTask</code> and return result in IDownloadCopletedListener callback
     *
     * @param request  - http url request
     * @param listener - callback (could be <code>null</code> to return the result (Http response string)
     */
    @Override
    public void executeRequest(final SamagraNetworkRequest request, @Nullable final IDownloadCompletedListener listener, @Nullable Type resultObjectClass) {
//        if(!NetworkService.getConnectivityInfo().isConnected(getApplicationContext())) {
//            if(listener != null) {
//                listener.onDownloaded(GBDownloaderStatusCodes.ERROR, null);
//            }
//            return;
//        }
        final SoftReference<IDownloadCompletedListener> listenerSoftReference = new SoftReference<IDownloadCompletedListener>(listener);
        final long startTime = System.currentTimeMillis();
        Grove.d(String.format("GBAsyncDataDownloader>>Request no %d added into queue for url %s %s",request.requestIndex,request.getType().name(),request.getEncodedUrlWithQueryParameters()));
        SamagraFetchDataTask downloadTask = new SamagraFetchDataTask(resultObjectClass) {
            @Override
            protected void onPostExecute(Object result) {
                if (getStatusCode() == SamagraDownloaderStatusCodes.SUCCESS)
                Grove.d(String.format("GBAsyncDataDownloader>>Request no %d finished with duration: %d for url %s %s",request.requestIndex,(int) (System.currentTimeMillis() - startTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));
                synchronized (SamagraAsyncDataDownloader.class) {  // synchronized is necessary to ensure that listener method will not be called here on null reference
                    try {
                        Grove.d("executeRequest()-->onPostExecute()-->removing request from active tasks");
                        Object removedObject = mActiveTasks.remove(request);
                        if (removedObject == null) {
                            Grove.d("executeRequest()-->onPostExecute()-->unable to remove request from active tasks as it is not present ");
                        }
                        Grove.d("executeRequest()-->onPostExecute()-->total activeRequests : " + mActiveTasks.size());
                        try {
                            IDownloadCompletedListener listener = listenerSoftReference.get();
                            if (listener != null) {
                                listener.onDownloaded(getStatusCode(),get());
                            } else {
                                Grove.d("onPostExecute()--> listener is null");
                            }
                        } catch (ExecutionException e) {
                            // TODO: do we need to resond to calling party via listener, if task get cancelled. (by system or user)
                            Grove.d("onPostExecute()--> ExecutionException occured : ",e);
                            listener.onDownloaded(SamagraDownloaderStatusCodes.ERROR,null);

                        } catch (InterruptedException e) {
                            Grove.d("onPostExecute()--> InterruptedException occured : ",e);
                            // TODO: do we need to resond to calling party via listener, if task get cancelled. (by system or user)
                            listener.onDownloaded(SamagraDownloaderStatusCodes.ERROR,null);
                        }
                    } catch (Exception ex) {
                        Grove.d("Exception occurred while returning callback : ",ex);
                    }
                }

            }

            /** {@inheritDoc} */
            @Override
            protected void onCancelled() {
                Grove.d("GBFetchDataTask.onCancelled() called");
                IDownloadCompletedListener listener = listenerSoftReference.get();
                if (listener != null) {
                    listener.onDownloaded(SamagraDownloaderStatusCodes.REQUEST_CANCELLED,null);
                }
            }
        };

        AsyncTask task = mActiveTasks.get(request);
        // to avoid multiple same network request until it is finished.
        if (task == null) {
            if (request.getRequestThreadPriority() == SamagraNetworkRequest.RequestThreadPriority.HIGH || request.getRequestThreadPriority() == SamagraNetworkRequest.RequestThreadPriority.URGENT) {
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,request);  // executing the request using AsyncTask
            } else {
                downloadTask.execute(request);  // executing the request using AsyncTask
            }
            mActiveTasks.put(request,downloadTask);
        } else {
            Grove.d("executeRequest()-->Task is already active for given request : " + request.getEncodedUrlWithQueryParameters());
        }
        Grove.d("executeRequest()-->total active requests : " + mActiveTasks.size());
    }

    /**
     * Call this method to remove it from active tasks.
     *
     * @param url - {String} the request url.
     */
    public void removeTaskFromActiveList(String url) {
        mActiveTasks.remove(url);
    }

    /**
     * Use this method to cancel task for particular url
     *
     * @param url - url which was already scheduled to fetch, but not finished yet.
     *            No exception will be thrown if this condition fails.
     */
    @Override
    public void cancel(String url) {
        Grove.d("GBAsyncDataDownloader.cancel() called");
        synchronized (SamagraAsyncDataDownloader.class) {
            Grove.d("Cancelling request for url :" + url);
            SamagraNetworkRequest request = new SamagraNetworkRequest();
            request.setBaseUrl(url);
            AsyncTask task = mActiveTasks.get(request);
            if (task != null) {
                task.cancel(true); // TODO: whether we should put this into try catch if task was already cancelled, what about if multiple task cancelation  request happened in concurrent
                mActiveTasks.remove(request);
            } else {
                Grove.d("No active task found to cancel for url :" + url);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void cancelRequest(SamagraNetworkRequest request) {
        Grove.d("cancelRequest()-->Cancelling Network request : " + request.getEncodedUrlWithQueryParameters());
        if (request != null) {
            AsyncTask task = mActiveTasks.get(request);
            if (task != null) {
                task.cancel(true); // TODO: whether we should put this into try catch if task was already cancelled, what about if multiple task cancelation  request happened in concurrent
                mActiveTasks.remove(request);
                Grove.d("cancelRequest()-->Request Cancelled :" + request.getEncodedUrlWithQueryParameters());
                Grove.d("cancelRequest()-->Total requests : " + mActiveTasks.size());
            } else {
                Grove.d("cancelRequest()-->No active task found to cancel for url :" + request.getEncodedUrlWithQueryParameters());
            }
        }
    }

    /**
     * Cancel all request which are active at this particular point of time
     */
    @Override
    public void cancelAll() {
        Grove.d("Cancelling all active request");
        synchronized (SamagraAsyncDataDownloader.class) {// here synchronization is needed because, if someone again put a new request in different thread, we don't want that request cancelled
            for (SamagraNetworkRequest key : mActiveTasks.keySet()) {
                AsyncTask task = mActiveTasks.get(key);
                task.cancel(true); // TODO: whether we should put this into try catch if task was already cancelled, what about if multiple task cancelation  request happened in concurrent
                Grove.d("Request cancelled for url :" + key);
                mActiveTasks.remove(key);
            }
        }
    }

    // Use this method if we want to cancel task which are in queue for executing, but not executing
    private void cancelAllPendingTask() {
        synchronized (SamagraAsyncDataDownloader.class) {  // here synchronization is needed because, if someone again put a new request in different thread, we don't want that request cancelled
            for (SamagraNetworkRequest key : mActiveTasks.keySet()) {
                AsyncTask task = mActiveTasks.get(key);
                task.cancel(false); // TODO: whether we should put this into try catch if task was already cancelled, what about if multiple task cancelation  request happened in concurrent
                mActiveTasks.remove(key);
            }
        }
    }

}
