package org.odk.collect.android.widgets.utilities;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.function.Consumer;

public class ViewModelAudioPlayer  {

    private final LifecycleOwner lifecycleOwner;

    public ViewModelAudioPlayer( LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

}
