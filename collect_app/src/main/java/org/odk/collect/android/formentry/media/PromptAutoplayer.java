package org.odk.collect.android.formentry.media;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.analytics.Analytics;
import org.odk.collect.android.audio.AudioHelper;
import org.odk.collect.android.utilities.WidgetAppearanceUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.odk.collect.android.analytics.AnalyticsEvents.PROMPT;
import static org.odk.collect.android.formentry.media.FormMediaUtils.getClipID;
import static org.odk.collect.android.formentry.media.FormMediaUtils.getPlayableAudioURI;
import static org.odk.collect.android.utilities.WidgetAppearanceUtils.NO_BUTTONS;

public class PromptAutoplayer {

    private static final String AUTOPLAY_ATTRIBUTE = "autoplay";
    private static final String AUDIO_OPTION = "audio";

    private final ReferenceManager referenceManager;
    private final Analytics analytics;
    private final String formIdentifierHash;

    public PromptAutoplayer(ReferenceManager referenceManager, Analytics analytics, String formIdentifierHash) {
        this.referenceManager = referenceManager;
        this.analytics = analytics;
        this.formIdentifierHash = formIdentifierHash;
    }





    private boolean appearanceDoesNotShowControls(String appearance) {
        return appearance.startsWith(WidgetAppearanceUtils.MINIMAL) ||
                appearance.startsWith(WidgetAppearanceUtils.COMPACT) ||
                appearance.contains(NO_BUTTONS);
    }


}
