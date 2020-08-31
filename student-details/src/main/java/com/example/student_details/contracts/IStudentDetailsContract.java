package com.example.student_details.contracts;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface IStudentDetailsContract {


    void launchProfileActivity(Context activityContext, int fragment_container, FragmentManager supportFragmentManager);
}
