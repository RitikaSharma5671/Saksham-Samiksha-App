package com.example.student_details.db;

import com.example.student_details.models.realm.SchoolEmployeesInfo;
import com.example.student_details.models.realm.StudentInfo;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = { StudentInfo.class, SchoolEmployeesInfo.class})
public
class StudentManagementRealmModule {

}
