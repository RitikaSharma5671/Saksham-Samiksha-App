package org.odk.collect.android.logic;

import androidx.annotation.Nullable;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public interface DeviceDetailsProvider {

    @Deprecated
    @Nullable
    String getDeviceId();

    @Nullable
    String getLine1Number();

    @Nullable
    String getSubscriberId();

    @Nullable
    String getSimSerialNumber();
}
