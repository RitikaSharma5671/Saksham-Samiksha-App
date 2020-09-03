package com.example.student_details.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.student_details.R
import com.example.student_details.modules.StudentDataModel
import kotlinx.android.synthetic.main.fragment_student_details.*
import java.util.*
import kotlin.collections.ArrayList

class StudentDetailsView : Fragment(), OnSelectListener, EditListener {

    private lateinit var studentAdapter: StudentAdapter
    private lateinit var studentDetailsViewModel: StudentDetailsViewModel

    private lateinit var selectedGrade: String
    private  lateinit var selectedSection : String
    private  var selectedSectionPosition:Int = 0
    private  var selectedGradePosition:Int = 0
    private lateinit var studentList: ArrayList<StudentInfo>
    private lateinit var masterList: ArrayList<StudentInfo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_student_details, container, false)
        studentDetailsViewModel =   getViewModelProvider(
                this,
                StudentDetailsViewModelFactory(
                        activity!!.application,""
                )
        )
                .get(StudentDetailsViewModel::class.java)
        studentDetailsViewModel.fetchStudentData()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentList = studentDetailsViewModel.fetchStudentData()
        masterList = ArrayList()
        masterList.addAll(studentList)
        setInitialData()
        initFilters()
        mark_attendance.setOnClickListener {
            val studentDetailsView = MarkAttendanceView()
            val bundle = Bundle()
            bundle.putSerializable("student_list", studentList)
            addFragment(R.id.fragment_container_1, parentFragmentManager, studentDetailsView, "StudentDetailsView")

        }
        Log.d("veev bertvbtr", "veet btb tre")
        val sd =  StudentDataModel()
        sd.fvf();
    }

    private fun addFragment(containerViewId: Int, manager: FragmentManager, fragment: Fragment, fragmentTag: String) {
        try {
            val fragmentName = fragment.javaClass.name
            //            Grove.d("addFragment() :: Adding new fragment %s", fragmentName);
            // Create new fragment and transaction
            val transaction = manager.beginTransaction()
            transaction.add(containerViewId, fragment, fragmentTag)
            transaction.addToBackStack(fragmentTag)
            Handler().post {
                try {
                    transaction.commit()
                } catch (ex: IllegalStateException) {
                }
            }
        } catch (ex: IllegalStateException) {
//            Grove.e("Failed to add Fragment with exception %s", ex.getMessage());
        }
    }
    private fun setInitialData() {
        studentAdapter = StudentAdapter(studentList, this, this, context!!)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity!!)
        student_list.layoutManager = mLayoutManager
        student_list.itemAnimator = DefaultItemAnimator()
        student_list.setAdapter(studentAdapter)
        (student_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    private fun initFilters() {
        val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                selectedGrade = grade_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                selectedSection = section_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                selectedSectionPosition = section_spinner.selectedItemPosition
                selectedGradePosition = grade_spinner.selectedItemPosition
                refreshData()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
        section_spinner.onItemSelectedListener = listener
        grade_spinner.onItemSelectedListener = listener
    }

    private fun filterList(grade: String, section: String): List<StudentInfo> {
        val filtered: MutableList<StudentInfo> = ArrayList<StudentInfo>()
        for (s in masterList) {
            try {
                if (s.standard.toString() == grade && s.section == section) {
                    filtered.add(s)
                }
            } catch (e: Exception) {
            }
        }
        return filtered
    }

    private fun refreshData() {
        // https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data?rq=1
        studentList.clear()
        val temp: List<StudentInfo> = filterList(selectedGrade, selectedSection)

        studentList.addAll(temp)
        studentAdapter.studentList = studentList
        studentAdapter.notifyDataSetChanged()
    }
    private fun getViewModelProvider(
            fragment: Fragment,
            factory: ViewModelProvider.Factory?
    ): ViewModelProvider {
        return ViewModelProviders.of(fragment, factory)
    }

    override fun onItemSelected(position: Int, isChecked: Boolean) {
        //
    }

    override fun onEditIconClicked(student: StudentInfo?) {
        TODO("Not yet implemented")
    }


}

class Com : Comparator<StudentInfo> {
    override fun compare(left: StudentInfo, right: StudentInfo): Int {
        if (left.studentName.toLowerCase().equals(right.studentName.toLowerCase())) {
            return  left.srn.compareTo(right.srn)
        }
        return left.studentName.toLowerCase().compareTo(right.studentName.toLowerCase())
    }

}

interface OnSelectListener {
    fun onItemSelected(position: Int, isChecked: Boolean)
}

interface EditListener {
    fun onEditIconClicked(student: StudentInfo?)
}
