package com.example.student_details.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.student_details.R
import com.example.student_details.databinding.FragmentStudentDetailsBinding
import com.example.student_details.getViewModelProvider
import com.example.student_details.models.realm.StudentInfo
import kotlinx.android.synthetic.main.fragment_student_details.*
import kotlin.collections.ArrayList

class StudentDetailsView : Fragment(), OnSelectListener, EditListener {

    private lateinit var studentAdapter: StudentAdapter
    private val studentDetailsViewModel: StudentDetailsViewModel by lazy {
        getViewModelProvider(this, StudentDetailsViewModelFactory(
                activity!!.application,""
        )).get(
                StudentDetailsViewModel::class.java
        )
    }

    private lateinit var selectedGrade: String
    private  lateinit var selectedSection : String
    private  var selectedSectionPosition:Int = 0
    private  var selectedGradePosition:Int = 0
    private val studentList = ArrayList<StudentInfo>()
    private lateinit var masterList: ArrayList<StudentInfo>
    private lateinit var mProgress: ProgressDialog

    private lateinit var layoutBinding: FragmentStudentDetailsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        layoutBinding.studentDetailsViewModel = studentDetailsViewModel
        layoutBinding.executePendingBindings()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilters()
        view.findViewById<ImageView>(R.id.close_cross).setOnClickListener {
            activity!!.finish()
        }
        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.sending_the_request))
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true
        initializeAdapter(layoutBinding)
        studentDetailsViewModel.fetchStudentData()
        studentDetailsViewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if(it == "true") {
                layoutBinding.studentParentLayout.isClickable = false
                layoutBinding.progressBar.visibility = View.VISIBLE
            }else {
                layoutBinding.studentParentLayout.isClickable = true
                layoutBinding.progressBar.visibility = View.GONE
            }
        })
        studentDetailsViewModel.studentsList.observe(viewLifecycleOwner, Observer {
            val productList = studentDetailsViewModel.studentsList.value
            when {
                productList != null && productList.isNotEmpty() -> {
                    layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.GONE
                    layoutBinding.studentList.visibility = View.VISIBLE
                    studentAdapter.submitList(productList)
                    studentAdapter.notifyDataSetChanged()
                }
                else -> {
                    layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.VISIBLE
                    layoutBinding.studentList.visibility = View.GONE
                    initializeAdapter(layoutBinding)
                }
            }
        })
        studentAdapter.submitList(studentList)
    }

    private fun initializeAdapter(binding: FragmentStudentDetailsBinding) {
        studentAdapter = StudentAdapter(
                viewLifecycleOwner,
                studentDetailsViewModel
        )
        binding.studentList.adapter = studentAdapter
        binding.studentList.itemAnimator = DefaultItemAnimator()
        ( binding.studentList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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
                context!!,
                R.layout.spinner_item, grades
        )

        val mPreconditionSpinner = layoutBinding.gradeSpinner
        mPreconditionSpinner.adapter = adapter
        mPreconditionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                studentDetailsViewModel.selectedGrade.postValue(grade_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1).toInt())
                selectedGrade = grade_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                if (selectedGrade == "11" || selectedGrade == "12") {
                    category.visibility = View.VISIBLE
                } else {
                    category.visibility = View.GONE
                }
                selectedGradePosition = grade_spinner.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
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
                context!!,
                R.layout.spinner_item, sections
        )

        val mPreconditionSpinner1 = layoutBinding.sectionSpinner
        mPreconditionSpinner1.adapter = adapter1
        mPreconditionSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                studentDetailsViewModel.selectedSection.postValue(section_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1))
                selectedSection = section_spinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                selectedSectionPosition = section_spinner.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                 //To change body of created functions use File | Settings | File Templates.
            }
        }

        category.setOnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked: Boolean = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                studentDetailsViewModel.selectedStream.postValue(checkedRadioButton.text.toString())
            } else {
                studentDetailsViewModel.selectedStream.postValue("")
            }
        }
    }

    private fun filterList(grade: String, section: String): List<StudentInfo> {
        val filtered: MutableList<StudentInfo> = ArrayList<StudentInfo>()
        for (s in masterList) {
            try {
                if (s.grade.toString() == grade && s.section == section) {
                    filtered.add(s)
                }
            } catch (e: Exception) {
            }
        }
        return filtered
    }

    private fun refreshData() {
        // https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data?rq=1
//        studentList.clear()
//        val temp: List<StudentInfo> = filterList(selectedGrade, selectedSection)
//
//        studentList.addAll(temp)
//        studentAdapter.studentList = studentList
//        studentAdapter.notifyDataSetChanged()
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
        if (left.name.toLowerCase().equals(right.name.toLowerCase())) {
            return  left.srn.compareTo(right.srn)
        }
        return left.name.toLowerCase().compareTo(right.name.toLowerCase())
    }

}

interface OnSelectListener {
    fun onItemSelected(position: Int, isChecked: Boolean)
}

interface EditListener {
    fun onEditIconClicked(student: StudentInfo?)
}
