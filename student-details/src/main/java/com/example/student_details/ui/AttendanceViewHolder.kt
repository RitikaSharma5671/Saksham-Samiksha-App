package com.example.student_details.ui

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.ItemAttendanceRowBinding
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.showNumberPickerDialog
import java.math.RoundingMode
import java.text.DecimalFormat

class AttendanceViewHolder(val itemAttendanceRowBinding: ItemAttendanceRowBinding, val application: Application, markAttendanceView: LifecycleOwner,
                           val markAttendanceViewModel: MarkAttendanceViewModel) : RecyclerView.ViewHolder(itemAttendanceRowBinding.root) {
    private lateinit var attendanceItemDataViewModel: AttendanceItemViewModel
    @SuppressLint("DefaultLocale")
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
        val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(studentInfo.name ), ContextCompat.getColor(context, R.color.color_primary))
        itemAttendanceRowBinding.listAvatar.setImageDrawable(drawable)
        itemAttendanceRowBinding.attendanceSwitch.isChecked = studentInfo.isPresent
        itemAttendanceRowBinding.listTitle.text = studentInfo.name.toUpperCase() + " (" + studentInfo.srn + ")"
        if(studentInfo.temp > 0)
        itemAttendanceRowBinding.tempValue.text = String.format(context!!.resources.getString(R.string.temp_value), studentInfo.temp.toString())
        itemAttendanceRowBinding.tempValue.setOnClickListener {
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
                        itemAttendanceRowBinding.tempValue.text = "$ff&#xb0;F"
                        markAttendanceViewModel.onTemperatureUpdated(studentInfo, ff)
                    }
            )
        }

        itemAttendanceRowBinding.attendanceSwitch.setOnCheckedChangeListener { _, _ ->
            if( itemAttendanceRowBinding.attendanceSwitch.isPressed) {
                // isPressed help in distinguishing whether priority toggled by user or done programmatically
                if ( itemAttendanceRowBinding.attendanceSwitch.isChecked) {
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
