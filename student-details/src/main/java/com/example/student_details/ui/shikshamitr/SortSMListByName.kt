package com.example.student_details.ui.shikshamitr

import com.example.student_details.models.realm.SchoolEmployeesAttendanceData
import com.example.student_details.models.realm.StudentInfo

class SortSMListByName() : Comparator<StudentInfo> {
    /** {@inheritDoc}  */
    override fun compare(o1: StudentInfo, o2: StudentInfo): Int {
        var result: Int = o1.name.compareTo(o2.name)
        if (result == 0) {
            result = o1.shikshaMitrName.compareTo(o2.shikshaMitrName)
        }
        return result
    }
}
