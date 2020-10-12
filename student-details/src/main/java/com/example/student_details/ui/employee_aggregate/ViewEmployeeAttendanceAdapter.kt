package com.example.student_details.ui.employee_aggregate

import android.app.Application
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities.convert
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.ViewAttendanceItemLayoutBinding
import com.example.student_details.ui.TextDrawable
import kotlinx.android.synthetic.main.view_attendance_item_layout.view.*

class ViewEmployeeAttendanceAdapter(
        private var studentDetailsViewModel: ViewEmployeeAttendanceViewModel
) : ListAdapter<EmpAttendanceData, ViewEmployeeAttendanceAdapter.ViewAttendanceViewHolder>(
        OnStudentDataDiffCallback()) {

    private lateinit var binding: ViewAttendanceItemLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAttendanceViewHolder {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.view_attendance_item_layout, parent, false
        )
        return ViewAttendanceViewHolder(
                binding,
                parent.context.applicationContext as Application,
                studentDetailsViewModel
        )
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.clearAnimation()
    }

    override fun onBindViewHolder(holder: ViewAttendanceViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo)
    }

    private fun initializeAnimationView(holder: ViewEmployeeAttendanceAdapter.ViewAttendanceViewHolder) {
        holder.itemView.findViewById<ImageView>(R.id.profile_pic_employee).setImageDrawable(null)
        holder.itemView.findViewById<TextView>(R.id.srn_number_employee).text = ""
        holder.itemView.findViewById<TextView>(R.id.name_student_employee).text = ""
        holder.itemView.findViewById<TextView>(R.id.attendance_status_present_employee).text = ""
    }

    inner class ViewAttendanceViewHolder(viewAttendanceItemLayoutBinding: ViewAttendanceItemLayoutBinding,
                                         val application: Application,
                                         val markAttendanceViewModel: ViewEmployeeAttendanceViewModel) :
            RecyclerView.ViewHolder(viewAttendanceItemLayoutBinding.root) {

        fun bind(attendanceData: EmpAttendanceData) {
            val context = itemView.context
            itemView.name_student_employee.text = "${convert(attendanceData.name)} (${attendanceData.misID})"
            itemView.srn_number_employee.text = attendanceData.designation
            val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(attendanceData.name), getColor(context, R.color.color_primary))
            itemView.profile_pic_employee.setImageDrawable(drawable)
            itemView.attendance_status_present_employee.text = if (attendanceData.attendanceStatus != "-" && attendanceData.attendanceStatus != "Others")
                attendanceData.attendanceStatus else attendanceData.attendanceStatus + " (" + attendanceData.otherReason + ")"
            if(attendanceData.attendanceStatus.toLowerCase() == "Present in school".toLowerCase())
                itemView.attendance_status_present_employee.setTextColor(getColor(context, R.color.color_primary))
            else
                itemView.attendance_status_present_employee.setTextColor(getColor(context, R.color.color2))

        }
    }
}

private class OnStudentDataDiffCallback :
        DiffUtil.ItemCallback<EmpAttendanceData>() {
    override fun areContentsTheSame(
            oldItem: EmpAttendanceData,
            newItem: EmpAttendanceData
    ): Boolean {
        return (oldItem.misID == newItem.misID) && (oldItem.present == newItem.present)
    }

    override fun areItemsTheSame(
            oldItem: EmpAttendanceData,
            newItem: EmpAttendanceData
    ): Boolean {
        return (oldItem.misID == newItem.misID) && (oldItem.present == newItem.present)
    }
}
