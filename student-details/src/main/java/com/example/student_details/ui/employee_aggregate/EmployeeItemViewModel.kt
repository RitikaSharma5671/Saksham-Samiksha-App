package com.example.student_details.ui.employee_aggregate

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.student_details.Utilities
import com.example.student_details.models.realm.StudentInfo

class EmployeeItemViewModel(
        private val application: Application,
        private val garmentInfo: StudentInfo
) : ViewModel() {


    val title: String by lazy {
        Utilities.convert(garmentInfo.name) + " ( " + garmentInfo.srn + ")"
    }

//    val srn: String by lazy {
//       garmentInfo.srn
//    }

    val subtitle: String by lazy {
        if (garmentInfo.grade <= 10) {
            garmentInfo.grade.toString() + " - " + garmentInfo.section + ""
        } else {
            garmentInfo.grade.toString() + " - " + garmentInfo.section + " (" + Utilities.convert(garmentInfo.stream) + ")"
        }

    }

}