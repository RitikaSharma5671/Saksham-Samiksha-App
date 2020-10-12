package com.example.student_details.ui.teacher_aggregate

import android.app.Application
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities.convert
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.ViewStudentAttendanceItemLayoutBinding
import com.example.student_details.ui.TextDrawable
import kotlinx.android.synthetic.main.view_student_attendance_item_layout.view.*

class ViewAttendanceAdapter(
        private var studentDetailsViewModel: ViewStudentAttendanceViewModel
) : ListAdapter<AttendanceData, ViewAttendanceAdapter.ViewAttendanceViewHolder>(
        OnStudentDataDiffCallback()) {

    private lateinit var binding: ViewStudentAttendanceItemLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAttendanceViewHolder {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.view_student_attendance_item_layout, parent, false
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

    private fun initializeAnimationView(holder: ViewAttendanceAdapter.ViewAttendanceViewHolder) {
        holder.itemView.findViewById<ImageView>(R.id.profile_pic).setImageDrawable(null)
        holder.itemView.findViewById<TextView>(R.id.srn_number).text = ""
        holder.itemView.findViewById<TextView>(R.id.name_student).text = ""
    }

    inner class ViewAttendanceViewHolder(viewAttendanceItemLayoutBinding: ViewStudentAttendanceItemLayoutBinding,
                                         val application: Application,
                                         val markAttendanceViewModel: ViewStudentAttendanceViewModel) :
            RecyclerView.ViewHolder(viewAttendanceItemLayoutBinding.root) {

        fun bind(attendanceData: AttendanceData) {
            val context = itemView.context
            itemView.name_student.text = convert(attendanceData.name)
            itemView.srn_number.text = attendanceData.srn
            val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(attendanceData.name), getColor(context, R.color.color_primary))
            itemView.profile_pic.setImageDrawable(drawable)
            itemView.attendance_status_present.visibility = if (attendanceData.present != null && attendanceData.present) View.VISIBLE else View.GONE
            itemView.attendance_status_absent.visibility = if (attendanceData.present != null && attendanceData.present) View.GONE else View.VISIBLE
        }
    }
}

private class OnStudentDataDiffCallback :
        DiffUtil.ItemCallback<AttendanceData>() {
    override fun areContentsTheSame(
            oldItem: AttendanceData,
            newItem: AttendanceData
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.present == newItem.present)
    }

    override fun areItemsTheSame(
            oldItem: AttendanceData,
            newItem: AttendanceData
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.present == newItem.present)
    }
}
