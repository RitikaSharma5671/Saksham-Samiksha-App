package com.psx.commons;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NetworkMonitor {

    private static MainApplication mainApplication = null;
    private static InternetObservingSettings internetObservingSettings = null;
    private static Disposable monitorSubscription = null;
    private static boolean lastConnectedState = false;

    public static void init(MainApplication mainApplication) {
        NetworkMonitor.mainApplication = mainApplication;
        internetObservingSettings = InternetObservingSettings.builder()
                .interval(5000)
                .strategy(new SocketInternetObservingStrategy())
                .host("www.google.com")
                .httpResponse(200)
                .timeout(5000)
                .errorHandler((exception, message) -> Timber.e(exception, "Exception in Lib message - %s", message))
                .build();
    }

    public static void init(MainApplication mainApplication, InternetObservingSettings internetObservingSettings) {
        NetworkMonitor.mainApplication = mainApplication;
        NetworkMonitor.internetObservingSettings = internetObservingSettings;
    }

    public static void startMonitoringInternet() throws InitializationException {
        checkValidConfig();
        Timber.d("Starting Monitoring");
        monitorSubscription = ReactiveNetwork
                .observeInternetConnectivity(internetObservingSettings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnectedToHost -> {
                    Timber.d("Here, Checking N/W");
                    if (isConnectedToHost != lastConnectedState) {
                        Timber.d("Is Connected To Host ? %s", isConnectedToHost);
                        String message = isConnectedToHost ? "Connected To Network" : "Lost Internet Connection";
                        NetworkIndicatorOverlay.make(mainApplication.getCurrentActivity(), message, 5000).show();
                        lastConnectedState = isConnectedToHost;
                    }
                }, throwable -> Timber.e(throwable, "Some error occurred %s", throwable.getMessage()));
    }

    public static void stopMonitoringInternet() throws InitializationException {
        checkValidConfig();
        if (!monitorSubscription.isDisposed()) {
            monitorSubscription.dispose();
            Timber.d("Monitor Disposed.");
        } else {
            Timber.w("Monitor Subscription already disposed.");
        }
    }

    private static void checkValidConfig() {
        if (mainApplication == null) {
            throw new InitializationException(NetworkMonitor.class, "NetworkMonitor not initialised. Please call init method.");
        }
    }
}
