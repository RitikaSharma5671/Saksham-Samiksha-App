package org.odk.collect.android.widgets.utilities;


import java.util.function.Consumer;

public interface AudioPlayer {

    void pause();

    void setPosition(String clipId, Integer position);

    void onPlayingChanged(String clipID, Consumer<Boolean> playingConsumer);

    void onPositionChanged(String clipID, Consumer<Integer> positionConsumer);

    void stop();
}
