package com.samagra.ancillaryscreens.network.infra;

import android.content.Context;

public interface IConnectivityInfo {
    /**
     * Check if there is any connectivity
     * @param context - application context
     * @return - true if connected (through any way - wifi, mobile)
     */
    boolean isConnected (Context context);

    /**
     * Check if there is any connectivity to a Wifi network
     * @param context
     * @return - returns true if connected to wifi
     */
    boolean isConnectedWifi (Context context);

    /**
     * Check if there is any connectivity to a mobile network
     * @return - returns true if connected to mobile
     */
    boolean isConnectedMobile (Context context);

    /**
     * Check if there is fast connectivity
     * @param context
     * @return - returns true if it is a fast connection
     */
    boolean isConnectedFast (Context context);

    /**
     * Return connection type
     * @return - string value of connection type
     */
    String getConnectionType();


}
