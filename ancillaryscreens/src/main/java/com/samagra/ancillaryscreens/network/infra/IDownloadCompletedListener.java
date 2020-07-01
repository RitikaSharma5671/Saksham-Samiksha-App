package com.samagra.ancillaryscreens.network.infra;

@FunctionalInterface
public interface IDownloadCompletedListener{
    void onDownloaded(SamagraDownloaderStatusCodes status, Object obj);
}