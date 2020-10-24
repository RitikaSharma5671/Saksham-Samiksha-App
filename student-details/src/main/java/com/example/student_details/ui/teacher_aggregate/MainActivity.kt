package com.example.student_details.ui.teacher_aggregate

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.student_details.R
import com.example.student_details.databinding.ActivityMainBinding
import com.example.student_details.getViewModelProvider
import com.samagra.grove.logging.Grove
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.fragment_student_details.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    private val calendar : Calendar = Calendar.getInstance()
    private var selectedDateStr: String = DateFormat.format("EEE, MMM d, yyyy", calendar).toString()
    private var currentSelectedDate: String =  DateFormat.format("yyyy-MM-dd",calendar).toString()
    private var selectedDay: String = DateFormat.format("EEE, MMM d, yyyy", calendar).toString().split(",")[0]
    private lateinit var viewAttendanceAdapter: ViewAttendanceAdapter
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var activityMainBinding: ActivityMainBinding
    private var selectedSectionPosition: Int = 0
    private var selectedGradePosition: Int = 0
    private lateinit var selectedGrade: String
    private  lateinit var selectedSection : String
    private var  userName : String = ""
    private val viewStudentAttendanceViewModel: ViewStudentAttendanceViewModel by lazy {
        getViewModelProvider(this).get(
                ViewStudentAttendanceViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -2)

        /* end after 2 months from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 2)

        // Default Date set to Today.
        val defaultSelectedDate = Calendar.getInstance()
        horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
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
        initFilters()
        activityMainBinding.filterCloseCross.setOnClickListener {
           finish()
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName  = sharedPreferences.getString("user.username", "")!!
        val schoolCode = sharedPreferences.getString("user.schoolCode", "")
        activityMainBinding.calendarView.visibility = View.VISIBLE
        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar, position: Int) {
                val selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString()
                currentSelectedDate = DateFormat.format("yyyy-MM-dd", date).toString()
                Grove.d( "$selectedDateStr selected!");
                viewStudentAttendanceViewModel.selectedDate.postValue(selectedDateStr)
                selectedDay = selectedDateStr.split(",")[0]

                viewStudentAttendanceViewModel.selectedDay.postValue(selectedDateStr.split(",")[0])
                viewStudentAttendanceViewModel.fetchRelevantStudentData(selectedDateStr.split(",")[0], currentSelectedDate, schoolCode!!)
//                Timber.i("onDateSelected, " + $selectedDateStr + " + " -  Position = "+  $position")
            }
        }
        initializeAdapter(activityMainBinding)
        viewStudentAttendanceViewModel.sundaySelected.observe(this, androidx.lifecycle.Observer {
            if (it != null && it == "Sunday Selected") {
                activityMainBinding.dividerViewAttendance1.visibility = View.GONE
                activityMainBinding.dividerViewAttendance.visibility = View.GONE
                activityMainBinding.attendanceSummary.visibility = View.GONE
                activityMainBinding.studentList.visibility = View.GONE
                activityMainBinding.progressBar.visibility = View.GONE
                activityMainBinding.noDataAvailable.visibility = View.GONE
                activityMainBinding.TodaySunday.visibility = View.VISIBLE

            }
        })
        viewStudentAttendanceViewModel.toastRender.observe(this,   androidx.lifecycle.Observer {
            if(it != null && it == "Render") {
                activityMainBinding.dividerViewAttendance1.visibility = View.GONE
                activityMainBinding.dividerViewAttendance.visibility = View.GONE
                activityMainBinding.attendanceSummary.visibility = View.GONE
                activityMainBinding.studentList.visibility = View.GONE
                activityMainBinding.progressBar.visibility = View.GONE
                activityMainBinding.noDataAvailable.visibility = View.GONE
                activityMainBinding.TodaySunday.visibility = View.GONE
                Toast.makeText(this, "Unable to fetch Attendance History for this selection", Toast.LENGTH_LONG).show()
            }
        })
        activityMainBinding.applySelection.setOnClickListener { viewStudentAttendanceViewModel.fetchRelevantStudentData(selectedDateStr.split(",")[0], currentSelectedDate, schoolCode!!) }
        viewStudentAttendanceViewModel.attendanceList.observe(this , androidx.lifecycle.Observer {
            val attendanceList = viewStudentAttendanceViewModel.attendanceList.value
            when {
                attendanceList != null && attendanceList.isNotEmpty() -> {
                    activityMainBinding.noDataAvailable.visibility = View.GONE
                    activityMainBinding.TodaySunday.visibility = View.GONE
                    activityMainBinding.studentList.visibility = View.VISIBLE
                    activityMainBinding.progressBar.visibility = View.GONE
                    activityMainBinding.dividerViewAttendance1.visibility = View.VISIBLE
                    activityMainBinding.dividerViewAttendance.visibility = View.VISIBLE
                    activityMainBinding.attendanceSummary.visibility = View.VISIBLE
                    viewAttendanceAdapter.submitList(attendanceList)
                    viewAttendanceAdapter.notifyDataSetChanged()
                    activityMainBinding.attendanceSummary.text = viewStudentAttendanceViewModel.totalStudentCount.value.toString() + "  STUDENTS, " + viewStudentAttendanceViewModel.absentStudents.value.toString() + "  ABSENT"
                }
                else -> {
                    activityMainBinding.noDataAvailable.visibility = View.VISIBLE
                    activityMainBinding.TodaySunday.visibility = View.GONE
                    activityMainBinding.studentList.visibility = View.GONE
                    activityMainBinding.progressBar.visibility = View.GONE
                    activityMainBinding.dividerViewAttendance1.visibility = View.GONE
                    activityMainBinding.dividerViewAttendance.visibility = View.GONE
                    activityMainBinding.attendanceSummary.visibility = View.GONE
                    initializeAdapter(activityMainBinding)
                }
            }        })
        viewStudentAttendanceViewModel.fetchRelevantStudentData(selectedDateStr.split(",")[0], currentSelectedDate, schoolCode!!)
    }

    private fun initFilters() {
        val grades = ArrayList<String>()
        grades.add("Class 1")
        grades.add("Class 2")
        grades.add("Class 3")
        grades.add("Class 4")
        grades.add("Class 5")
        grades.add("Class 6")
        grades.add("Class 7")
        grades.add("Class 8")
        grades.add("Class 9")
        grades.add("Class 10")
        grades.add("Class 11")
        grades.add("Class 12")
        val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, grades
        )

        val mPreconditionSpinner = activityMainBinding.gradeSpinner
        mPreconditionSpinner.adapter = adapter
        mPreconditionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewStudentAttendanceViewModel.selectedGrade.postValue(grade_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1].toInt())
                selectedGrade = grade_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1]
                if (selectedGrade == "11" || selectedGrade == "12") {
                    category.visibility = View.VISIBLE
                } else {
                    category.visibility = View.GONE
                }
                selectedGradePosition = grade_spinner.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val sections = ArrayList<String>()
        sections.add("Section A")
        sections.add("Section B")
        sections.add("Section C")
        sections.add("Section D")
        sections.add("Section E")
        sections.add("Section F")
        sections.add("Section G")
        sections.add("Section H")
        val adapter1 = ArrayAdapter(
                this,
                R.layout.spinner_item, sections
        )

        val mPreconditionSpinner1 = activityMainBinding.sectionSpinner
        mPreconditionSpinner1.adapter = adapter1
        mPreconditionSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewStudentAttendanceViewModel.selectedSection.postValue(section_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1])
                selectedSection = section_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1]
                selectedSectionPosition = section_spinner.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                 //To change body of created functions use File | Settings | File Templates.
            }
        }

        category.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            val isChecked: Boolean = checkedRadioButton.isChecked
            if (isChecked) {
                viewStudentAttendanceViewModel.selectedStream.postValue(checkedRadioButton.text.toString())
            } else {
                viewStudentAttendanceViewModel.selectedStream.postValue("")
            }
        }
    }

    private fun initializeAdapter(activityMainBinding: ActivityMainBinding) {
        viewAttendanceAdapter = ViewAttendanceAdapter(
                viewStudentAttendanceViewModel
        )
        activityMainBinding.studentList.adapter = viewAttendanceAdapter
        activityMainBinding.studentList.itemAnimator = DefaultItemAnimator()
        ( activityMainBinding.studentList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

}