package com.example.student_details.ui.teacher_attendance;

import com.example.student_details.models.realm.StudentInfo;

import java.util.Comparator;

public class SortByName1 implements Comparator<StudentInfo> {
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(StudentInfo o1, StudentInfo o2) {
        int result = o1.getName().compareTo(o2.getName());
        if (result == 0) {
            result = o1.getSrn().compareTo(o2.getSrn());
        }
        return result;
    }
}