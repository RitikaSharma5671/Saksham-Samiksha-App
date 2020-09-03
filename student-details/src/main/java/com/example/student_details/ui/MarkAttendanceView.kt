package com.example.student_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.example.student_details.databinding.FragmentMarkAttendanceBinding
import kotlinx.android.synthetic.main.fragment_mark_attendance.*

class MarkAttendanceView : Fragment() {

    private lateinit var layoutBinding: FragmentMarkAttendanceBinding
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val markAttendanceViewModel: MarkAttendanceViewModel by lazy {
        getViewModelProvider(this, null).get(
                MarkAttendanceViewModel::class.java
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)
        layoutBinding.markAttendanceViewModel = markAttendanceViewModel
        layoutBinding.executePendingBindings()
        return layoutBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentForClass = arguments!!.getString("student_list") as ArrayList<StudentInfo>
        initializeAdapter(layoutBinding)
    }

    private fun initializeAdapter(binding: FragmentMarkAttendanceBinding) {
//        attendanceAdapter = AttendanceAdapter(
//                viewLifecycleOwner,
//                onRackViewModel,
//                closetAnalyticsEventsHandler,
//                OnTheRackEventsListener(context!!, binding.rackGarmentsList)
//        )
//        binding.rackGarmentsList.adapter = adapter
//        binding.rackGarmentsList.itemAnimator =
//                OnRackGarmentItemAnimator(binding.rackGarmentsList)
    }


    private fun getViewModelProvider(
            fragment: Fragment,
            factory: ViewModelProvider.Factory?
    ): ViewModelProvider {
        return ViewModelProviders.of(fragment)
    }


}
