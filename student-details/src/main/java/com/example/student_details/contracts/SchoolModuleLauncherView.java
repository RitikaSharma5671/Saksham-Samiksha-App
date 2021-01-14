package com.example.student_details.contracts;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.student_details.R;
import com.example.student_details.ui.ClassFilterFragment;
import com.example.student_details.ui.StudentDetailsView;
import com.example.student_details.ui.teacher_attendance.MarkTeacherAttendanceView;

public class SchoolModuleLauncherView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa);
        if(getIntent().getStringExtra("nameOfActivity").equals("studentData")){
            StudentDetailsView studentDetailsView = new StudentDetailsView();
            addFragment(R.id.fragment_container_1, getSupportFragmentManager(), studentDetailsView, "StudentDetailsView");
        }else if(getIntent().getStringExtra("nameOfActivity").equals("markStudentAttendance")){
            ClassFilterFragment classFilterFragment = new ClassFilterFragment();
            addFragment(R.id.fragment_container_1, getSupportFragmentManager(), classFilterFragment, "ClassFilterFragment");
        }else if(getIntent().getStringExtra("nameOfActivity").equals("markTeacherAttendance")){
            MarkTeacherAttendanceView classFilterFragment = new MarkTeacherAttendanceView();
            addFragment(R.id.fragment_container_1, getSupportFragmentManager(), classFilterFragment, "MarkTeacherAttendanceView");

        }
//        ClassFilterFragment studentDetailsView = new ClassFilterFragment();
//        addFragment(R.id.fragment_container_1, getSupportFragmentManager(), studentDetailsView, "ClassFilterFragment");

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0 ) {
            fm.getBackStackEntryAt(0);
            if (fm.getBackStackEntryAt(0).getName() != null && (fm.getBackStackEntryAt(0).getName().equals("StudentDetailsView")
            || fm.getBackStackEntryAt(0).getName().equals("MarkTeacherAttendanceView")
            || fm.getBackStackEntryAt(0).getName().equals("ClassFilterFragment"))) {
               finish();
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }

}
