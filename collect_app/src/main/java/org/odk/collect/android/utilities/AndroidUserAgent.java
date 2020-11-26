package org.odk.collect.android.utilities;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        return String.format("%s/%s %s",
                "com.samagra.sakshamSamiksha",
                "BuildConfig.VERSION_NAME",
                System.getProperty("http.agent"));
    }

}
