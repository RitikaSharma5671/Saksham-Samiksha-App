package com.example.student_details.ui

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.databinding.ItemAttendanceRowBinding

class AttendanceViewHolder(val itemAttendanceRowBinding: ItemAttendanceRowBinding, val application: Application, markAttendanceView: LifecycleOwner) : RecyclerView.ViewHolder(itemAttendanceRowBinding.root) {
    private lateinit var attendanceItemDataViewModel: AttendanceItemViewModel
    fun bind(studentInfo: StudentInfo, layoutPosition: Int) {
        val context = itemView.context
        attendanceItemDataViewModel = AttendanceItemViewModel(
                application,
                studentInfo
        )
        with(itemAttendanceRowBinding) {
            attendanceItemViewModel = attendanceItemDataViewModel
            executePendingBindings()
        }
    }
}
