package com.example.student_details.ui.employee_aggregate

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.student_details.R
import com.example.student_details.databinding.ActivityEmployeeAttendanceBinding
import com.example.student_details.getViewModelProvider
import com.samagra.grove.logging.Grove
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.activity_employee_attendance.*
import timber.log.Timber
import java.util.*

class ViewEmployeeAttendance : AppCompatActivity() {
    private val calendar: Calendar = Calendar.getInstance()
    private var selectedDateStr: String = DateFormat.format("EEE, MMM d, yyyy", calendar).toString()
    private var currentSelectedDate: String = DateFormat.format("yyyy-MM-dd", calendar).toString()
    private var selectedDay: String = DateFormat.format("EEE, MMM d, yyyy", calendar).toString().split(",")[0]
    private lateinit var viewAttendanceAdapter: ViewEmployeeAttendanceAdapter
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var activityBinding: ActivityEmployeeAttendanceBinding
    private var selectedSectionPosition: Int = 0
    private var selectedGradePosition: Int = 0
    private lateinit var selectedGrade: String
    private lateinit var selectedSection: String
    private var userName: String = ""
    private val viewStudentAttendanceViewModel: ViewEmployeeAttendanceViewModel by lazy {
        getViewModelProvider(this).get(
                ViewEmployeeAttendanceViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_attendance)

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -2)

        /* end after 2 months from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 2)

//        Default Date set to Today.
        val defaultSelectedDate = Calendar.getInstance()
        horizontalCalendar = HorizontalCalendar.Builder(this, R.id.employee_attendance_calendar_view)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .build()
        Timber.i(DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString())
        employee_attendance_close_icon.setOnClickListener {
            finish()
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        val schoolCode: String = sharedPreferences.getString("user.schoolCode", "")!!
        userName = sharedPreferences.getString("user.username", "")!!
        employee_attendance_calendar_view.visibility = View.VISIBLE
        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar, position: Int) {
                selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString()
                currentSelectedDate = DateFormat.format("yyyy-MM-dd", date).toString()
                Grove.d("$selectedDateStr selected!");
                viewStudentAttendanceViewModel.selectedDate.postValue(selectedDateStr)
                selectedDay = selectedDateStr.split(",")[0]

                viewStudentAttendanceViewModel.selectedDay.postValue(selectedDateStr.split(",")[0])
                viewStudentAttendanceViewModel.fetchRelevantStudentData(selectedDateStr.split(",")[0], currentSelectedDate, schoolCode)
                Timber.i("onDateSelected, $selectedDateStr  - Position = $position")
            }
        }
        initializeAdapter()
        viewStudentAttendanceViewModel.fetchRelevantStudentData(selectedDateStr.split(",")[0], currentSelectedDate, schoolCode)
        viewStudentAttendanceViewModel.sundaySelected.observe(this, androidx.lifecycle.Observer {
            if (it != null && it == "Sunday Selected") {
                employee_attendance_divider_view_attendance1.visibility = View.GONE
                employee_attendance_divider_view_attendance.visibility = View.GONE
                employee_attendance_attendance_summary.visibility = View.GONE
                employee_attendance_emp_list.visibility = View.GONE
                employee_attendance_progress_bar.visibility = View.GONE
                employee_attendance_header_no_data_available.visibility = View.GONE
                employee_attendance_header_today_sunday.visibility = View.VISIBLE
            }
        })

        viewStudentAttendanceViewModel.isProgressBarVisible.observe(this, androidx.lifecycle.Observer {
            if (it)
                employee_attendance_progress_bar.visibility = View.VISIBLE
            else
                employee_attendance_progress_bar.visibility = View.GONE

        })
        viewStudentAttendanceViewModel.isStudentListVisible.observe(this, androidx.lifecycle.Observer {
            if (it)
                employee_attendance_emp_list.visibility = View.VISIBLE
            else
                employee_attendance_emp_list.visibility = View.GONE

        })
        viewStudentAttendanceViewModel.isSundayMessageVisible.observe(this, androidx.lifecycle.Observer {
            if (it)
                employee_attendance_header_today_sunday.visibility = View.VISIBLE
            else
                employee_attendance_header_today_sunday.visibility = View.GONE

        })
        viewStudentAttendanceViewModel.isNoStudentDataMessageVisible.observe(this, androidx.lifecycle.Observer {
            if (it)
                employee_attendance_header_no_data_available.visibility = View.VISIBLE
            else
                employee_attendance_header_no_data_available.visibility = View.GONE

        })

        viewStudentAttendanceViewModel.toastRender.observe(this, androidx.lifecycle.Observer {
            if (it != null && it == "Render") {
                employee_attendance_divider_view_attendance1.visibility = View.GONE
                employee_attendance_divider_view_attendance1.visibility = View.GONE
                employee_attendance_attendance_summary.visibility = View.GONE
                employee_attendance_emp_list.visibility = View.GONE
                employee_attendance_progress_bar.visibility = View.GONE
                employee_attendance_header_no_data_available.visibility = View.GONE
                employee_attendance_header_today_sunday.visibility = View.GONE
                Toast.makeText(this, "Unable to fetch Attendance History for this selection", Toast.LENGTH_LONG).show()
            }
        })
        viewStudentAttendanceViewModel.attendanceList.observe(this, androidx.lifecycle.Observer {
            val attendanceList = viewStudentAttendanceViewModel.attendanceList.value
            when {
                attendanceList != null && attendanceList.isNotEmpty() -> {
                    employee_attendance_header_no_data_available.visibility = View.GONE
                    employee_attendance_header_today_sunday.visibility = View.GONE

                    employee_attendance_emp_list.visibility = View.VISIBLE
                    employee_attendance_progress_bar.visibility = View.GONE
                    employee_attendance_divider_view_attendance1.visibility = View.VISIBLE
                    employee_attendance_divider_view_attendance.visibility = View.VISIBLE
                    employee_attendance_attendance_summary.visibility = View.VISIBLE
                    viewAttendanceAdapter.submitList(attendanceList)
                    viewAttendanceAdapter.notifyDataSetChanged()
                    employee_attendance_attendance_summary.text = viewStudentAttendanceViewModel.totalStudentCount.value.toString() + "  EMPLOYEES, " + viewStudentAttendanceViewModel.presentInSchoolCount.value.toString() + "  PRESENT IN SCHOOL"
                }
                else -> {
                    employee_attendance_header_no_data_available.visibility = View.VISIBLE
                    employee_attendance_header_today_sunday.visibility = View.GONE
                    employee_attendance_emp_list.visibility = View.GONE
                    employee_attendance_progress_bar.visibility = View.GONE
                    employee_attendance_divider_view_attendance.visibility = View.GONE
                    employee_attendance_divider_view_attendance1.visibility = View.GONE
                    employee_attendance_attendance_summary.visibility = View.GONE
                    initializeAdapter()
                }
            }
        })

    }

    //
    private fun initializeAdapter() {
        viewAttendanceAdapter = ViewEmployeeAttendanceAdapter(
                viewStudentAttendanceViewModel
        )
        employee_attendance_emp_list.adapter = viewAttendanceAdapter
        employee_attendance_emp_list.itemAnimator = DefaultItemAnimator()
        (employee_attendance_emp_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

}