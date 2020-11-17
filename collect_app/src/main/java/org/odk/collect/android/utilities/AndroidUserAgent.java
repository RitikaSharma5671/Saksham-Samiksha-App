package org.odk.collect.android.utilities;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        return String.format("%s/%s %s",
                "org.odk.collect.android",
                BuildConfig.VERSION_NAME,
                System.getProperty("http.agent"));
    }

}
