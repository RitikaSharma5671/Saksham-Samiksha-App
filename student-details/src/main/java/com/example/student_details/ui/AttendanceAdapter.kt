package com.example.student_details.ui

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.databinding.ItemAttendanceRowBinding


class AttendanceAdapter(
        private var markAttendanceView: LifecycleOwner,
        private var markAttendanceViewModel: MarkAttendanceViewModel
) : ListAdapter<StudentInfo, AttendanceViewHolder>(
        OnAttendanceDataDiffCallback()) {

    private lateinit var binding: ItemAttendanceRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_attendance_row, parent, false
        )
        return AttendanceViewHolder(
                binding,
                parent.context.applicationContext as Application,
                markAttendanceView
        )    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo, position)
    }

    private fun initializeAnimationView(holder: AttendanceViewHolder) {

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.clearAnimation()
    }

}

private class OnAttendanceDataDiffCallback :
        DiffUtil.ItemCallback<StudentInfo>() {
    override fun areContentsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn)
    }

    override fun areItemsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn)
    }
}
