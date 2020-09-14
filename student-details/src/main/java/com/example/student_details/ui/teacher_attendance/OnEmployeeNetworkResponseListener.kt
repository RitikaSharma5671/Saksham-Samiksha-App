package com.example.student_details.ui.teacher_attendance

import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo

interface OnEmployeeNetworkResponseListener {
        fun onSuccess(employeeInfo: EmployeeInfo)
        fun onFailure(e: Exception)

}