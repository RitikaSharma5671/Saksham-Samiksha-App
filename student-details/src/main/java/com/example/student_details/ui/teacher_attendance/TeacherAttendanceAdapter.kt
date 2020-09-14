package com.example.student_details.ui.teacher_attendance

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
import com.example.student_details.databinding.ItemTeacherAttendanceRowBinding
import com.example.student_details.models.realm.SchoolEmployeesInfo


class TeacherAttendanceAdapter(
        private var markAttendanceView: LifecycleOwner,
        private var markAttendanceViewModel: MarkTeacherAttendanceViewModel
) : ListAdapter<SchoolEmployeesInfo, TeacherAttendanceViewHolder>(
        OnTeacherAttendanceDataDiffCallback()) {

    private lateinit var binding: ItemTeacherAttendanceRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherAttendanceViewHolder {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_teacher_attendance_row, parent, false
        )
        return TeacherAttendanceViewHolder(
                binding,
                parent.context.applicationContext as Application,
                markAttendanceViewModel
        )    }

    override fun onBindViewHolder(holder: TeacherAttendanceViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo, position)
    }

    private fun initializeAnimationView(holder: TeacherAttendanceViewHolder) {
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

private class OnTeacherAttendanceDataDiffCallback :
        DiffUtil.ItemCallback<SchoolEmployeesInfo>() {
    override fun areContentsTheSame(
            oldItem: SchoolEmployeesInfo,
            newItem: SchoolEmployeesInfo
    ): Boolean {
        return (oldItem.employeeId == newItem.employeeId) && (oldItem.isPresent == newItem.isPresent) && (oldItem.temp == newItem.temp)
    }

    override fun areItemsTheSame(
            oldItem: SchoolEmployeesInfo,
            newItem: SchoolEmployeesInfo
    ): Boolean {
        return (oldItem.employeeId == newItem.employeeId)&& (oldItem.isPresent == newItem.isPresent) && (oldItem.temp == newItem.temp)
    }
}
