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

        public SignalExchangeObject(Modules to, Modules from, Intent intentToLaunch) {
            super(ExchangeObjectTypes.SIGNAL, to, from);
            this.intentToLaunch = intentToLaunch;
        }

        public SignalExchangeObject(Modules to, Modules from, Intent intentToLaunch, boolean shouldStartAsNewTask) {
            super(ExchangeObjectTypes.SIGNAL, to, from);
            this.intentToLaunch = intentToLaunch;
            this.shouldStartAsNewTask = shouldStartAsNewTask;
        }
    }

    public static class DataExchangeObject<T> extends ExchangeObject {
        public T data;

        private DataExchangeObject(Modules to, Modules from, T data) {
            super(ExchangeObjectTypes.DATA_EXCHANGE, to, from);
            this.data = data;
        }
    }

    public static class EventExchangeObject extends ExchangeObject {

        public CustomEvents customEvents;

        public EventExchangeObject(Modules to, Modules from, CustomEvents customEvents) {
            super(ExchangeObjectTypes.EVENT, to, from);
            this.customEvents = customEvents;
        }
    }

    public enum ExchangeObjectTypes {
        SIGNAL, // Indicates a signal to start some new Activity through an intent. The class to start is passed in the data field
        DATA_EXCHANGE, // Indicates Data Exchange between Modules
        EVENT; // Indicates an event has occurred.
    }
}
