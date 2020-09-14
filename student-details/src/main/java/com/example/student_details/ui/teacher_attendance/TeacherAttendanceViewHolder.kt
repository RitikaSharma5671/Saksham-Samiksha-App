package com.example.student_details.ui.teacher_attendance


import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.ItemTeacherAttendanceRowBinding
import com.example.student_details.models.realm.SchoolEmployeesInfo
import com.example.student_details.showNumberPickerDialog
import com.example.student_details.ui.TextDrawable
import java.math.RoundingMode
import java.text.DecimalFormat

class TeacherAttendanceViewHolder(val itemTeacherAttendanceRowBinding: ItemTeacherAttendanceRowBinding,
                                  val application: Application, val markAttendanceViewModel: MarkTeacherAttendanceViewModel) :
        RecyclerView.ViewHolder(itemTeacherAttendanceRowBinding.root) {
    private lateinit var attendanceItemDataViewModel: TeacherAttendanceItemViewModel
    fun bind(studentInfo: SchoolEmployeesInfo, layoutPosition: Int) {
        val context = itemView.context
        attendanceItemDataViewModel = TeacherAttendanceItemViewModel(
                studentInfo
        )
        with(itemTeacherAttendanceRowBinding) {
            teacherAttendanceItemViewModel = attendanceItemDataViewModel
            executePendingBindings()
        }
        val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(studentInfo.name ), ContextCompat.getColor(context, R.color.color_primary))
        itemTeacherAttendanceRowBinding.listAvatar.setImageDrawable(drawable)
        itemTeacherAttendanceRowBinding.attendanceSwitch.isChecked = studentInfo.isPresent
        if(studentInfo.temp > 0)
            itemTeacherAttendanceRowBinding.tempValue.text = String.format(context!!.resources.getString(R.string.temp_value), studentInfo.temp.toString())
        itemTeacherAttendanceRowBinding.tempValue.setOnClickListener {
            showNumberPickerDialog(
                    itemView.context,
                    title = "Select the Temperature",
                    value = 98.4, // in kilograms
                    range = 97.0 .. 103.0,
                    stepSize = 0.1,
                    formatToString = fun(it: Double): String {
                        val num = it
                        val df = DecimalFormat("#.###")
                        df.roundingMode = RoundingMode.CEILING
                        val ff = df.format(num)
                        return "$ff F"
                    },
                    valueChooseAction = fun(it: Double) {
                        val num = it
                        val df = DecimalFormat("#.###")
                        df.roundingMode = RoundingMode.CEILING
                        val ff = df.format(num)
                        itemTeacherAttendanceRowBinding.tempValue.text = "$ff&#xb0;F"
                        markAttendanceViewModel.onTemperatureUpdated(studentInfo, ff)
                    }
            )
        }

        itemTeacherAttendanceRowBinding.attendanceSwitch.setOnCheckedChangeListener { _, _ ->
            if( itemTeacherAttendanceRowBinding.attendanceSwitch.isPressed) {
                // isPressed help in distinguishing whether priority toggled by user or done programmatically
                if ( itemTeacherAttendanceRowBinding.attendanceSwitch.isChecked) {
                    markAttendanceViewModel.onPrioritySwitchClicked(1, studentInfo)
                } else {
                    markAttendanceViewModel.onPrioritySwitchClicked(0, studentInfo)
                }
            }
        }

//        itemAttendanceRowBinding.tempButton.setOnClickListener {
//            showNumberPickerDialog(
//                    itemView.context,
//                    title = "Select the Temperature",
//                    value = 98.4, // in kilograms
//                    range = 97.0 .. 103.0,
//                    stepSize = 0.1,
//                    formatToString = fun(it: Double): String {
//                        val num = it
//                        val df = DecimalFormat("#.###")
//                        df.roundingMode = RoundingMode.CEILING
//                        val ff = df.format(num)
//                        return "$ff F"
//                    },
//                    valueChooseAction = fun(it: Double) {
//                        val num = it
//                        val df = DecimalFormat("#.###")
//                        df.roundingMode = RoundingMode.CEILING
//                        val ff = df.format(num)
//                        itemAttendanceRowBinding.tempValue.text = "$ff F"
//                        markAttendanceViewModel.onTemperatureUpdated(studentInfo, ff)
//                    }
//            )
//        }
    }

}
