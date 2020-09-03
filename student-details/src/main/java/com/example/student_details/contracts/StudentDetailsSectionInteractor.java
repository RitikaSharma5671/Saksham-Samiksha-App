package com.example.student_details.contracts;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.student_details.ui.ClassFilterFragment;
import com.example.student_details.ui.StudentDetailsView;

import java.util.ArrayList;

public class StudentDetailsSectionInteractor implements IStudentDetailsContract {

    @Override
    public void launchProfileActivity(Context activityContext,
                                      int fragment_container, FragmentManager supportFragmentManager) {

        ClassFilterFragment studentDetailsView = new ClassFilterFragment();
        addFragment(fragment_container, supportFragmentManager, studentDetailsView, "ClassFilterFragment");

    }

    private void addFragment(int containerViewId, FragmentManager manager, Fragment fragment, String fragmentTag) {
        try {
            final String fragmentName = fragment.getClass().getName();
//            Grove.d("addFragment() :: Adding new fragment %s", fragmentName);
            // Create new fragment and transaction
            final FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(containerViewId, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            new Handler().post(() -> {
                try {
                    transaction.commit();
                } catch (IllegalStateException ex) {
                }
            });
        } catch (IllegalStateException ex) {
//            Grove.e("Failed to add Fragment with exception %s", ex.getMessage());

        }
    }

}