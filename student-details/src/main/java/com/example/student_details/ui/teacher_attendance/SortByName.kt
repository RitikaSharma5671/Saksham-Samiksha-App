package com.example.student_details.ui.teacher_attendance

import com.example.student_details.models.realm.SchoolEmployeesInfo

class SortByName() : Comparator<SchoolEmployeesInfo> {
    /** {@inheritDoc}  */
    override fun compare(o1: SchoolEmployeesInfo, o2: SchoolEmployeesInfo): Int {
        var result: Int = o1.name.compareTo(o2.name)
        if (result == 0) {
            result = o1.designation.compareTo(o2.designation)
        }
        return result
    }
}
