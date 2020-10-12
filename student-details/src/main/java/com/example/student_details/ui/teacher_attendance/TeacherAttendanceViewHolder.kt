@file:Suppress("SENSELESS_COMPARISON")

package com.example.student_details.ui.teacher_attendance

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.ItemTeacherAttendanceRowBinding
import com.example.student_details.models.realm.SchoolEmployeesAttendanceData
import com.example.student_details.showNumberPickerDialog
import com.example.student_details.ui.TextDrawable
import com.samagra.grove.logging.Grove
import java.math.RoundingMode
import java.text.DecimalFormat


class TeacherAttendanceViewHolder(private val itemTeacherAttendanceRowBinding: ItemTeacherAttendanceRowBinding,
                                  val application: Application, val markAttendanceViewModel: MarkTeacherAttendanceViewModel,
                                  val markAttendanceView: Fragment) :
        RecyclerView.ViewHolder(itemTeacherAttendanceRowBinding.root) {
    private lateinit var attendanceItemDataViewModel: TeacherAttendanceItemViewModel
    fun bind(studentInfo: SchoolEmployeesAttendanceData) {
        val context = itemView.context
        attendanceItemDataViewModel = TeacherAttendanceItemViewModel(
                studentInfo
        )
        with(itemTeacherAttendanceRowBinding) {
            teacherAttendanceItemViewModel = attendanceItemDataViewModel
            executePendingBindings()
        }
        val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(studentInfo.name), ContextCompat.getColor(context, R.color.color_primary))
        itemTeacherAttendanceRowBinding.listAvatar.setImageDrawable(drawable)
        if (studentInfo.temp > 0)
            itemTeacherAttendanceRowBinding.tempValue.text = String.format(context!!.resources.getString(R.string.temp_value), studentInfo.temp.toString())
        itemTeacherAttendanceRowBinding.tempValue.setOnClickListener {
            showNumberPickerDialog(
                    itemView.context,
                    title = "Select the Temperature",
                    value = 98.4, // in kilograms
                    range = 97.0..103.0,
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

        val rvr = ArrayList<String>()
        rvr.add("Present in School")
        rvr.add("On-Leave")
        rvr.add("Absent")
        rvr.add("Working From Home")
        rvr.add("Present (On Duty)")
        rvr.add("On Training")
        rvr.add("Others")
        var presenceStatus = "Mark Attendance"
         if (rvr.contains(studentInfo.attendanceStatus))
            presenceStatus = studentInfo.attendanceStatus
        if (studentInfo.attendanceStatus == "Others") {
            itemTeacherAttendanceRowBinding.otherLayout.visibility = View.VISIBLE
            if (studentInfo.otherReason.isNotEmpty()) {
                itemTeacherAttendanceRowBinding.otherStatus.setText(studentInfo.otherReason)
                itemTeacherAttendanceRowBinding.otherStatus.setTextColor(ContextCompat.getColor(context, R.color.color1))
            } else {
                itemTeacherAttendanceRowBinding.otherStatus.setText("")
                itemTeacherAttendanceRowBinding.otherStatus.setTextColor(ContextCompat.getColor(context, R.color.color5))
            }
        } else {
            itemTeacherAttendanceRowBinding.otherLayout.visibility = View.GONE
        }

        val content = SpannableString(presenceStatus)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        itemTeacherAttendanceRowBinding.attendanceStatus.text = content
        if (presenceStatus.toLowerCase() == "Mark Attendance".toLowerCase()) {
            itemTeacherAttendanceRowBinding.attendanceStatus.setTextColor(ContextCompat.getColor(context, R.color.color5))
        } else {
            itemTeacherAttendanceRowBinding.attendanceStatus.setTextColor(ContextCompat.getColor(context, R.color.color1))

        }
        val selectedIndex = when {
            studentInfo.attendanceStatus == "" || studentInfo.attendanceStatus == "Mark Attendance" -> {
                -1
            }
            rvr.contains(studentInfo.attendanceStatus) -> {
                rvr.indexOf(studentInfo.attendanceStatus)
            }
            else -> {
                6
            }
        }
        var changedValue: String
        itemTeacherAttendanceRowBinding.editIcon.setOnClickListener {
            val listItems = arrayOf("Present in School", "On-Leave", "Absent", "Working From Home", "Present (On Duty)", "On Training", "Others")
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("SELECT APPROPRIATE STATUS")
            builder.setSingleChoiceItems(listItems, selectedIndex, fun(dialog: DialogInterface, which: Int) {
                changedValue = listItems[which]
                dialog.dismiss()
                if (changedValue != "" && changedValue != studentInfo.attendanceStatus) {
                    markAttendanceViewModel.onPrioritySwitchClicked(changedValue, studentInfo)
                }
                Grove.d("Position: " + which + " Value: " + listItems[which])
            })
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }

        itemTeacherAttendanceRowBinding.otherBtn.setOnClickListener {
            val view: View? = markAttendanceView.activity?.currentFocus
            if (markAttendanceView != null && view != null) {
                val imm: InputMethodManager = markAttendanceView.activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            if(itemTeacherAttendanceRowBinding.otherStatus.text != null &&
                    itemTeacherAttendanceRowBinding.otherStatus.text.toString().isNotEmpty() &&
                    itemTeacherAttendanceRowBinding.otherStatus.text.toString() != "Enter Status")
            markAttendanceViewModel.onOtherReasonChanged(itemTeacherAttendanceRowBinding.otherStatus.text.toString(), studentInfo)
        }

        itemTeacherAttendanceRowBinding.attendanceStatus.setOnClickListener {
            val listItems = arrayOf("Present in School", "On-Leave", "Absent", "Working From Home", "Present (On Duty)", "On Training", "Others")
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Choose Status")
            builder.setSingleChoiceItems(listItems, selectedIndex, fun(dialog: DialogInterface, which: Int) {
                changedValue = listItems[which]
                dialog.dismiss()
                if (changedValue != "" && changedValue != studentInfo.attendanceStatus) {
                    if (changedValue == "Others") {
                        itemTeacherAttendanceRowBinding.otherLayout.visibility = View.VISIBLE
                    } else {
                        itemTeacherAttendanceRowBinding.otherLayout.visibility = View.GONE
                    }
                    markAttendanceViewModel.onPrioritySwitchClicked(changedValue, studentInfo)
                }
                Grove.d("Position: " + which + " Value: " + listItems[which])
            })
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
    }
}