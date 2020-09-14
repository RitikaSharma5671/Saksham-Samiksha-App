package com.example.student_details.ui.teacher_attendance

import androidx.lifecycle.ViewModel
import com.example.student_details.Utilities
import com.example.student_details.models.realm.SchoolEmployeesInfo

class TeacherAttendanceItemViewModel(
        private val schoolEmployeesInfo: SchoolEmployeesInfo
) : ViewModel() {


    val title: String by lazy {
        Utilities.convert(schoolEmployeesInfo.name)+ " (" + schoolEmployeesInfo.employeeId + ")"
    }

    val subtitle: String by lazy {
        schoolEmployeesInfo.designation

    }

}