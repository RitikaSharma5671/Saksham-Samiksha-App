package com.samagra.ancillaryscreens.network.infra;

import androidx.annotation.NonNull;

import com.samagra.ancillaryscreens.data.network.BackendApiUrls;
import com.samagra.grove.logging.Grove;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalDataFetcher {

    /**
     * The static map of the download listeners for count updates.
     */
    private static ConcurrentHashMap<Class, IDownloadCompletedListener> sAppInfoDownloadListeners = new ConcurrentHashMap<> ();

    /**
     * The static map of the download listeners for rewards updates.
     */
    private static HashMap<Class, IDownloadCompletedListener> sRolesInfoDownloadListenersMap = new HashMap<> ();


    /**
     * static count listener instance.
     */
    private static AppInfoDownloadListener sAppInfoDownloadListener = new AppInfoDownloadListener ();

    /**
     * Static listener for the user rewards.
     */
    private static RolesInfoDownloadListener sRolesInfoDownloadListener = new RolesInfoDownloadListener();

    /**
     * Number of call that are required to sync user data to backend
     */
    private static final int USER_SYNC_CALL_NORMAL_COUNT = 2;
    private static final int USER_SYNC_CALL_NEW_ACCOUNT_COUNT = 1;

    /**
     * private constructor.
     */
    private GlobalDataFetcher () {
    }


    /**
     * Fetch the user data and set it in the required location.
     *
     * @param listener  - {@link IUserSyncListener} which is called after the entire data is fetched.
     * @param options   - {Object...} varargs object which will be passed back with the listener.
     */
    public static void fetchUserData(boolean isLoggedIn, @NonNull IUserSyncListener listener, Object... options) {
        UserSyncNotifier syncNotifier;
        if(isLoggedIn) {
            syncNotifier = new UserSyncNotifier (USER_SYNC_CALL_NORMAL_COUNT, listener, options);
            attachAppInfoListener(GlobalDataFetcher.class, syncNotifier);
            attachRolesInfoDownloadListener(GlobalDataFetcher.class, syncNotifier);
            GlobalDataFetcher.getApplicationInfo();
            GlobalDataFetcher.getRolesInfo();
        }else{
            syncNotifier = new UserSyncNotifier (USER_SYNC_CALL_NEW_ACCOUNT_COUNT, listener, options);
            attachAppInfoListener(GlobalDataFetcher.class, syncNotifier);
            GlobalDataFetcher.getApplicationInfo();
        }
    }


    public static void fetchUserDataLoggedInUser(@NonNull IUserSyncListener listener, Object... options) {
        UserSyncNotifier syncNotifier;
        syncNotifier = new UserSyncNotifier(USER_SYNC_CALL_NEW_ACCOUNT_COUNT, listener, options);
        attachRolesInfoDownloadListener(GlobalDataFetcher.class, syncNotifier);
        GlobalDataFetcher.getRolesInfo();
    }


    /**
     * Update the closet count for the user. This is usually triggered by the other pieces of
     * code like product add, hold etc.
     */
    public static void getApplicationInfo() {
//        if (GBAppConfig.getUUID () == null || "0".equals (GBAppConfig.getUUID ())) {
//            sCountListener.onDownloaded (GBDownloaderStatusCodes.ERROR, null);
//            return;
//        }
        try {
            String url = BackendApiUrls.APP_ENDPOINT;
            SamagraNetworkRequest samagraNetworkRequest = new SamagraNetworkRequest(url);
            samagraNetworkRequest.setRequestThreadPriority (SamagraNetworkRequest.RequestThreadPriority.HIGH);
            NetworkService.getDownloader ().executeRequest (samagraNetworkRequest, sAppInfoDownloadListener, Object.class);
        } catch (NullPointerException ex) {
            Grove.e ("Failed to send update closet count request with " + ex.getMessage ());
        }
    }

    /**
     * Attach a custom listener to be called on update of the closet count.
     *
     * @param aClass   - {Class} the class that is using the count.
     * @param listener - {IDownloadCompletedListener} implementation.
     */
    public static void attachAppInfoListener(Class aClass, IDownloadCompletedListener listener) {
        if (listener != null) {
            sAppInfoDownloadListeners.put (aClass, listener);
        }
    }

    /**
     * Detach a listener that is attached for listening on the closet count updates.
     *
     * @param aClass - {Class} the class whose listener needs to be detached.
     * @return {IDownloadCompletedListener} the listener attached or null.
     */
    public static IDownloadCompletedListener detachAppInfoListener(Class aClass) {
        return sAppInfoDownloadListeners.remove (aClass);
    }

    public static void attachRolesInfoDownloadListener(Class aClass, IDownloadCompletedListener listener) {
        sRolesInfoDownloadListenersMap.put (aClass, listener);
    }


    public static void detachRolesInfoListener(Class aClass) {
        sRolesInfoDownloadListenersMap.remove (aClass);
    }

    /**
     * AppInfoDownloadListener which is a local implementation of the {@link IDownloadCompletedListener} which
     * on count request updates the count object of the app configuration.
     */
    private static class AppInfoDownloadListener implements IDownloadCompletedListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDownloaded (SamagraDownloaderStatusCodes status, Object obj) {
            if (status == SamagraDownloaderStatusCodes.SUCCESS) {

                if (obj != null) {
                   Grove.d ("From GlobalDataFetcher: successful fetch of closet count - " + obj.toString ());
                }
            } else {
            }
            Iterator iterator = sAppInfoDownloadListeners.keySet ().iterator ();
            Class aClass;
            while (iterator.hasNext ()) {
                aClass = (Class) iterator.next ();
                sAppInfoDownloadListeners.get (aClass).onDownloaded (status, obj);
            }
        }
    }


    public static void getRolesInfo() {
        try {
            String url = BackendApiUrls.ROLE_ENDPOINT;
            SamagraNetworkRequest samagraNetworkRequest = new SamagraNetworkRequest(url);
            samagraNetworkRequest.setRequestThreadPriority (SamagraNetworkRequest.RequestThreadPriority.HIGH);
            NetworkService.getDownloader ().executeRequest (samagraNetworkRequest, sRolesInfoDownloadListener, Object.class);
        } catch (NullPointerException ex) {
            Grove.e ("Failed to get user rewards points : " + ex.getMessage ());
        }
    }




    private static class RolesInfoDownloadListener implements IDownloadCompletedListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDownloaded (SamagraDownloaderStatusCodes status, Object obj) {
            if (status == SamagraDownloaderStatusCodes.SUCCESS) {
                if (obj != null) {
//                    RewardDetails.PointsSummary pointsSummary = ((LoyaltyResponse) obj).getRewardDetails ().getPointsSummary ();
                    Grove.d ("From GlobalDataFetcher: user rewards points  - " + obj);
//                    GBAppConfig.setUserRewardsPoints (pointsSummary.getBalance ());
//                    GBAppConfig.setRewardsUnit (pointsSummary.getNearestReward ().getUnit ());
//                    GBAppConfig.setNearestRewards (pointsSummary.getNearestReward ().getQuantity ());
//                    GBAppConfig.setPointsNeeded (pointsSummary.getNearestReward ().getRemainingPoints ());
                }

            }
            Iterator iterator = sRolesInfoDownloadListenersMap.keySet ().iterator ();
            Class aClass;
            while (iterator.hasNext ()) {
                aClass = (Class) iterator.next ();
                sRolesInfoDownloadListenersMap.get (aClass).onDownloaded (status, obj);
            }
        }
    }



    /**
     * Internal class that is used for user data sync operation.
     */
    private static class UserSyncNotifier implements IDownloadCompletedListener {

        private final int mQueueCount;
        private int mCurrentCount = 0;
        private IUserSyncListener mListener;
        private Object[] mOptions;

        /**
         * Constructor which takes in all the required parameters.
         *
         * @param queueCount - {int} the total number of requests.
         * @param listener   - {IUserSyncListener} the listener object.
         * @param options    - {Object...} varargs of object which are passed back.
         */
        public UserSyncNotifier (int queueCount, IUserSyncListener listener, Object... options) {
            mQueueCount = queueCount;
            mListener = listener;
            mOptions = options;
        }

        /**
         * This is called when the download of each individual request is completed. We check
         * if the required number of requests are completed and call the
         * {@link IUserSyncListener#onUserDataSynced(Object...)} passing as arguments the options
         * that are specified.
         * {@inheritDoc}
         */
        @Override
        public void onDownloaded (SamagraDownloaderStatusCodes status, Object obj) {
            if (obj == null) {
                return;
            }
            mCurrentCount++; // increment the count
            Grove.d ("UserSync - Downloaded : " + mCurrentCount + " , QUEUE_TOTAL : " + mQueueCount);
            if (mCurrentCount == mQueueCount) {
                // detach all listeners attached for this sync call.
                if(mQueueCount == 2){
                    detachRolesInfoListener(GlobalDataFetcher.class);
                }
                detachAppInfoListener(GlobalDataFetcher.class);
                // if all the queues are cleared, then call the listener with options.
                Grove.d ("UserSync - Completed all download calling the listener.");
                if (mListener != null) {
                    mListener.onUserDataSynced (mOptions);
                }

            }
        }
    }





}
