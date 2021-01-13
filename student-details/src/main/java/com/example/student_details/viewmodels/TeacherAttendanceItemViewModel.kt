package com.example.student_details.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.example.student_details.Utilities
import com.example.student_details.models.realm.SchoolEmployeesAttendanceData
import com.example.student_details.models.realm.SchoolEmployeesInfo

class TeacherAttendanceItemViewModel(
        private val schoolEmployeesInfo: SchoolEmployeesAttendanceData
) : ViewModel() {

    val title: String by lazy {
        Utilities.convert(schoolEmployeesInfo.name)+ " (" + schoolEmployeesInfo.employeeId + ")"
    }

    val subtitle: String by lazy {
        schoolEmployeesInfo.designation
    }

    val otherReason: String by lazy {
        if(schoolEmployeesInfo.otherReason != null&&schoolEmployeesInfo.otherReason != "")  schoolEmployeesInfo.otherReason else "Enter Status"
    }

}