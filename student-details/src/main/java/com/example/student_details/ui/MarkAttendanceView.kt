package com.example.student_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.student_details.R
import kotlinx.android.synthetic.main.fragment_mark_attendance.*

class MarkAttendanceView : Fragment() {
    lateinit var attendanceAdapter: AttendanceAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_mark_attendance, container, false)
//        studentDetailsViewModel =   getViewModelProvider(
//                this,
//                StudentDetailsViewModelFactory(
//                        activity!!.application,""
//                )
//        )
//                .get(StudentDetailsViewModel::class.java)
//        studentDetailsViewModel.falseetchStudentData()
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentForClass = arguments!!.getString("studentList") as ArrayList<StudentInfo>
        attendanceAdapter = AttendanceAdapter(context!!, studentForClass)
        rootContainer.setAdapter(attendanceAdapter)
    }


}
