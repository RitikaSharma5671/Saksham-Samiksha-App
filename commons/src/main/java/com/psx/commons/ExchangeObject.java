package com.psx.commons;

import android.content.Intent;

/**
 * Exchange object is used to exchange data between two modules.
 */
public class ExchangeObject {
    public ExchangeObjectTypes type;
    public Modules to;
    public Modules from;

    private ExchangeObject(ExchangeObjectTypes type, Modules to, Modules from) {
        this.type = type;
        this.to = to;
        this.from = from;
    }

    public static class SignalExchangeObject extends ExchangeObject {

        public Intent intentToLaunch;
        public boolean shouldStartAsNewTask = false;

        public SignalExchangeObject(ExchangeObjectTypes type, Modules to, Modules from, Intent intentToLaunch) {
            super(type, to, from);
            this.intentToLaunch = intentToLaunch;
        }

        public SignalExchangeObject(ExchangeObjectTypes type, Modules to, Modules from, Intent intentToLaunch, boolean shouldStartAsNewTask) {
            super(type, to, from);
            this.intentToLaunch = intentToLaunch;
            this.shouldStartAsNewTask = shouldStartAsNewTask;
        }
    }

    public static class DataExchangeObject<T> extends ExchangeObject {
        public T data;

        private DataExchangeObject(ExchangeObjectTypes type, Modules to, Modules from, T data) {
            super(type, to, from);
            this.data = data;
        }
    }

    public enum ExchangeObjectTypes {
        SIGNAL, // Indicates a signal to start some new Activity through an intent. The class to start is passed in the data field
        DATA_EXCHANGE; // Indicates Data Exchange between Modules
    }
}
