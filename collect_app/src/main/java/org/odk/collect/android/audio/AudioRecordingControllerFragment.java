package org.odk.collect.android.audio;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.odk.collect.android.R;
import org.odk.collect.android.analytics.AnalyticsEvents;
import org.odk.collect.android.databinding.AudioRecordingControllerFragmentBinding;
import org.odk.collect.android.formentry.FormEntryViewModel;
import org.odk.collect.android.injection.DaggerUtils;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.odk.collect.android.utilities.DialogUtils.showIfNotShowing;

public class AudioRecordingControllerFragment extends Fragment {



    @Inject
    FormEntryViewModel.Factory formEntryViewModelFactory;

    public AudioRecordingControllerFragmentBinding binding;
     private FormEntryViewModel formEntryViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);

        formEntryViewModel = new ViewModelProvider(requireActivity(), formEntryViewModelFactory).get(FormEntryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AudioRecordingControllerFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
