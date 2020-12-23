package org.odk.collect.android.widgets.utilities;

import android.app.Activity;
import android.util.Pair;

import androidx.lifecycle.LifecycleOwner;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.analytics.AnalyticsEvents;
import org.odk.collect.android.formentry.FormEntryViewModel;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.utilities.FormEntryPromptUtils;
import org.odk.collect.android.utilities.PermissionUtils;
import org.odk.collect.android.utilities.QuestionMediaManager;

import java.util.function.Consumer;

public class InternalRecordingRequester implements RecordingRequester {

    private final Activity activity;
    private final PermissionUtils permissionUtils;
    private final LifecycleOwner lifecycleOwner;
    private final QuestionMediaManager questionMediaManager;
    private final FormEntryViewModel formEntryViewModel;

    public InternalRecordingRequester(Activity activity, PermissionUtils permissionUtils, LifecycleOwner lifecycleOwner, QuestionMediaManager questionMediaManager, FormEntryViewModel formEntryViewModel) {
        this.activity = activity;
        this.permissionUtils = permissionUtils;
        this.lifecycleOwner = lifecycleOwner;
        this.questionMediaManager = questionMediaManager;
        this.formEntryViewModel = formEntryViewModel;
    }

    @Override
    public void onIsRecordingBlocked(Consumer<Boolean> isRecordingBlockedListener) {

    }

    @Override
    public void requestRecording(FormEntryPrompt prompt) {
        permissionUtils.requestRecordAudioPermission(activity, new PermissionListener() {
            @Override
            public void granted() {
                String quality = FormEntryPromptUtils.getAttributeValue(prompt, "quality");

            }

            @Override
            public void denied() {

            }
        });

        formEntryViewModel.logFormEvent(AnalyticsEvents.AUDIO_RECORDING_INTERNAL);
    }

    @Override
    public void onRecordingInProgress(FormEntryPrompt prompt, Consumer<Pair<Long, Integer>> durationListener) {

    }

    @Override
    public void onRecordingFinished(FormEntryPrompt prompt, Consumer<String> recordingAvailableListener) {

    }
}
