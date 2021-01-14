package com.example.student_details.ui

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.databinding.ItemAttendanceRowBinding
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.viewmodels.MarkStudentAttendanceViewModel


class AttendanceAdapter(
        private var markAttendanceView: LifecycleOwner,
        private var markAttendanceViewModel: MarkStudentAttendanceViewModel
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
                markAttendanceView,
                markAttendanceViewModel
        )    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo, position)
    }

    private fun initializeAnimationView(holder: AttendanceViewHolder) {
        holder.itemView.findViewById<ImageView>(R.id.list_avatar).setImageDrawable(null)
        holder.itemView.findViewById<TextView>(R.id.list_title).text = ""
        holder.itemView.findViewById<TextView>(R.id.list_desc).text= ""
        holder.itemView.findViewById<SwitchCompat>(R.id.attendance_switch).isChecked = false
        holder.itemView.findViewById<TextView>(R.id.temp_value).text = holder.itemView.context.resources.getString(R.string.empty_t)
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
        return (oldItem.srn == newItem.srn) && (oldItem.isPresent == newItem.isPresent) && (oldItem.temp == newItem.temp)
    }

    override fun areItemsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn)&& (oldItem.isPresent == newItem.isPresent) && (oldItem.temp == newItem.temp)
    }
}
