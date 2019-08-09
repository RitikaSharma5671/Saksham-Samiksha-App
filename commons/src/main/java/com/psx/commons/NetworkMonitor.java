package com.psx.commons;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
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
                .timeout(10000)
                .errorHandler(new ErrorHandler() {
                    @Override
                    public void handleError(Exception exception, String message) {
                        Timber.e(exception, "Exception in Lib message - %s", message);
                    }
                })
                .build();
    }

    public static void init(MainApplication mainApplication, InternetObservingSettings internetObservingSettings) {
        NetworkMonitor.mainApplication = mainApplication;
        NetworkMonitor.internetObservingSettings = internetObservingSettings;
    }

    public static void startMonitoringInternet() {
        checkValidConfig();
        Timber.d("Starting Monitoring");
        monitorSubscription = ReactiveNetwork
                .observeInternetConnectivity(internetObservingSettings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnectedToHost -> {
                    Timber.d("Here, Checking N/W");
                    if (isConnectedToHost != lastConnectedState) {
                        // TODO : Show Network info overlay
                        Timber.d("Is Connected To Host ? %s", isConnectedToHost);
                        String message = isConnectedToHost ? "Connected To Network" : "Lost Internet Connection";
                        NetworkIndicatorOverlay.make(mainApplication.getCurrentActivity(), message, 5000).show();
                        lastConnectedState = isConnectedToHost;
                    }
                });
    }

    public static void stopMonitoringInternet() {
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
