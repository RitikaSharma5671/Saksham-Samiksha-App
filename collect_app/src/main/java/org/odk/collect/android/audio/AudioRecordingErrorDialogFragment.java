package org.odk.collect.android.audio;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.odk.collect.android.R;
import org.odk.collect.android.injection.DaggerUtils;

import javax.inject.Inject;

public class AudioRecordingErrorDialogFragment extends DialogFragment {



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                .setPositiveButton(R.string.ok, null);



        return dialogBuilder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
