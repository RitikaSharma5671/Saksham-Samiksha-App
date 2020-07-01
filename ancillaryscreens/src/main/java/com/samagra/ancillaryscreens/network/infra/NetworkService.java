package com.samagra.ancillaryscreens.network.infra;

public class NetworkService {

    //TODO: IDownloader should be renamed with some manager like RequestManager, Client, or simply a Service
    private static IDownloader downloader = null;  // A generic IDownloader which manage all requests tasks
    private static IConnectivityInfo connectivityInfo;  // Connectivity Interface
    private static SamagraHttpClient httpClient;  // HttpClient to make blocking http request
    public static final int READ_TIMEOUT = 30000;   // constants for connection read time out
    // (Recommended 10-30sec Otherwise we are blocking user to do something else)
    public static final int CONNECTION_TIMEOUT = 15000; // constants for connection time out


    /**
     * Use this method if you are trying to simulate some of the connectivity behaviour. Mainly used in Unit testing
     * @param info
     */
    public static void setConnectivityInfo(IConnectivityInfo info)
    {
        connectivityInfo = info;
    }


    /**
     * Use this method if you are trying to simulate some of the http response behaviour. Mainly used in Unit testing
     * @param httpClient
     */
    public static void setGBHttpClient(SamagraHttpClient httpClient)
    {
        NetworkService.httpClient = httpClient;
    }


    /**
     * return an http client method which is used to make http/https requests
     * @return - default implementation of GBHttpClient
     */
    public static SamagraHttpClient getHttpClient()
    {
        if(httpClient == null)
        {
            httpClient = new SamagraHttpClient(READ_TIMEOUT,CONNECTION_TIMEOUT);
        }
        return httpClient;
    }

    /**
     * return IDownloader instance for making one or several network request and getting response in background
     * @return - defaul implementation i.e. GBAsyncDataDownloader
     */
    public static IDownloader getDownloader() {
        if (downloader == null) {
            synchronized (NetworkService.class) {
                if (downloader == null) {
                    downloader = new SamagraAsyncDataDownloader();
                }
            }
        }
        return downloader;
    }

    /**
     * return instance of Connectivity
     * @return
     */
    public static IConnectivityInfo getConnectivityInfo()
    {
        if(connectivityInfo == null)
        {
            connectivityInfo = new ConnectivityInfo();
        }
        return connectivityInfo;
    }



    public static void setDownloader(IDownloader downloader) {
        NetworkService.downloader = downloader;
    }



}
