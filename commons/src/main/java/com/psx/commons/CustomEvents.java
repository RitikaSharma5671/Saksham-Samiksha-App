package com.psx.commons;

/**
 * Enum of all the 'events' that are sent on the {@link RxBus} for inter module communication.
 *
 * @author Pranav Sharma
 */
public enum CustomEvents {
    LOGOUT_COMPLETED, // Signals logout is completed
    LOGOUT_INITIATED, // Signals logout is initiated
    INTERNET_DISCONNECTED, // Signals that internet connectivity has lost
    INTERNET_CONNECTED; // Signals that internet connectivity is gained
}
