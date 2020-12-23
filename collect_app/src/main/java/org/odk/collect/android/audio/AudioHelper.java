package org.odk.collect.android.audio;

import android.media.MediaPlayer;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import org.odk.collect.android.widgets.utilities.AudioPlayer;
import org.odk.collect.utilities.async.Scheduler;

import java.util.function.Supplier;

/**
 * Object for setting up playback of audio clips with {@link AudioButton} and
 * controls. Only one clip can be played at once so when a clip is
 * played from a view or from the `play` method any currently playing audio will stop.
 * <p>
 * Clips are identified using a `clipID` which enables the playback state of clips to survive
 * configuration changes etc. Two views should not use the same `clipID` unless they are intended
 * to have the same playback state i.e. when one is played the other also appears to be playing.
 * This allows for different controls to play the same file but not appear to all be playing at once.
 * <p>
 * An {@link AudioHelper} instance is designed to live at an {@link android.app.Activity} level.
 * However, the underlying implementation uses a {@link androidx.lifecycle.ViewModel} so it is safe to
 * construct multiple instances (within a {@link android.view.View} or
 * {@link androidx.fragment.app.Fragment} for instance) if needed within one
 * {@link android.app.Activity}.
 *
 * @deprecated wrapping the ViewModel like this doesn't really fit with other ways we've integrated
 * widgets with "external" services. Instead of this widgets should talk to {@link AudioPlayer}
 * and the Activity/Fragment components should talk to the ViewModel itself.
 */

@Deprecated
public class AudioHelper {

    private final LifecycleOwner lifecycleOwner;

    public AudioHelper(FragmentActivity activity, LifecycleOwner lifecycleOwner, Scheduler scheduler, Supplier<MediaPlayer> mediaPlayerFactory) {
        this.lifecycleOwner = lifecycleOwner;
    }


    private static class AudioButtonListener implements AudioButton.Listener {


        @Override
        public void onPlayClicked() {

        }

        @Override
        public void onStopClicked() {

        }
    }

    private static class BackgroundObserver implements LifecycleObserver {


    }
}
