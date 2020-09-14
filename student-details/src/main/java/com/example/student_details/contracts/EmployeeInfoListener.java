package com.example.student_details.contracts;

import com.example.student_details.models.realm.SchoolEmployeesInfo;

import java.util.List;

public interface EmployeeInfoListener {
    void onSuccess(List<SchoolEmployeesInfo> employees);
    void onFailure();
}
