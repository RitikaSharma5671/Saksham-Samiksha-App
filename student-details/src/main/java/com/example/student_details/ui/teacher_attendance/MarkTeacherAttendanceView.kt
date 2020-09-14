package com.example.student_details.ui.teacher_attendance

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.student_details.R
import com.example.student_details.databinding.FragmentMarkTeacherAttendanceBinding
import com.example.student_details.getViewModelProvider
import com.example.student_details.models.realm.SchoolEmployeesInfo
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.ui.SamagraAlertDialog1

class MarkTeacherAttendanceView : Fragment() {
    private lateinit var layoutBinding: FragmentMarkTeacherAttendanceBinding
    private lateinit var attendanceAdapter: TeacherAttendanceAdapter
    private val studentList: ArrayList<SchoolEmployeesInfo> = ArrayList()
    private lateinit var mProgress: ProgressDialog
    private val markAttendanceViewModel: MarkTeacherAttendanceViewModel by lazy {
        getViewModelProvider(this).get(
                MarkTeacherAttendanceViewModel::class.java
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = FragmentMarkTeacherAttendanceBinding.inflate(inflater, container, false)
        layoutBinding.markTeacherAttendanceViewModel = null
        layoutBinding.executePendingBindings()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markAttendanceViewModel.fetchEmployeeData()
        layoutBinding.vffv.setOnClickListener {
            markAttendanceViewModel.onSendAttendanceClicked()
        }
        layoutBinding.teacherAttendanceProgressBar.visibility = View.VISIBLE
        layoutBinding.studentsList.visibility = View.GONE
        layoutBinding.rl1.visibility = View.GONE
        layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.GONE
        layoutBinding.vffv.visibility = View.GONE
        layoutBinding.closeTeacherAttendanceScreen.setOnClickListener {
            activity!!.finish()
        }
        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.sending_the_request))
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.setCanceledOnTouchOutside(false)
        mProgress.isIndeterminate = true
        initializeAdapter(layoutBinding)

        markAttendanceViewModel.showIncompleteAlertDialog.observe(viewLifecycleOwner, Observer {
            if (it) {
                SamagraAlertDialog1.Builder(context!!).setTitle("INCOMPLETE DATA").setMessage("Please check there are some incomplete entries for the employees.\n Click Ok to fill the data")
                        .setAction2("OK", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                alertDialog.dismiss()
                            }

                        }).show()
            }
        })

        markAttendanceViewModel.attendanceUploadSuccessful.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it == "Success") {
                    mProgress.dismiss()
                    SamagraAlertDialog1.Builder(context!!).setTitle("DATA SUBMITTED SUCCESSFULLY").setMessage("The student data has been successfully submitted.\n Click OK to go back to Home Screen.")
                            .setAction2("YES, PLEASE", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                                override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                    alertDialog.dismiss()
                                    activity!!.finish()
                                }

                            }).show()
                } else if (it == "Failure") {
                    mProgress.dismiss()
                    SamagraAlertDialog1.Builder(context!!).setTitle("DATA SUBMISSION FAILED").setMessage("The student data could not be submitted.\n Click OK to try sending data again.")
                            .setAction2("OK", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                                override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                    alertDialog.dismiss()
                                }

                            }).show()

                }
            }
        })
        markAttendanceViewModel.showCompleteDialog.observe(viewLifecycleOwner, Observer {
            if (it) {
                SamagraAlertDialog1.Builder(context!!).setTitle("SEND STUDENT DATA").setMessage("Please ensure that you have entered correct data for the students.")
                        .setAction2("YES, PLEASE", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                alertDialog.dismiss()
                                mProgress.show()
                                markAttendanceViewModel.uploadAttendanceData()
                            }

                        }).setAction3("CANCEL, WANT TO RECHECK", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                alertDialog.dismiss()
                            }

                        }).show()
            }
        })
        markAttendanceViewModel.employeeList.observe(viewLifecycleOwner, Observer {
            val employeeList = markAttendanceViewModel.employeeList.value
            when {
                employeeList != null && employeeList.isNotEmpty() -> {
                    attendanceAdapter.submitList(employeeList)
                    attendanceAdapter.notifyDataSetChanged()
                    layoutBinding.teacherAttendanceProgressBar.visibility = View.GONE
                    layoutBinding.rl1.visibility = View.VISIBLE
                    layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.GONE
                    layoutBinding.vffv.visibility = View.VISIBLE
                    layoutBinding.studentsList.visibility = View.VISIBLE
                }
                else -> {
                    initializeAdapter(layoutBinding)
                    layoutBinding.studentsList.visibility = View.GONE
                    layoutBinding.rl1.visibility = View.GONE
                    layoutBinding.teacherAttendanceProgressBar.visibility = View.GONE
                    layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.VISIBLE
                    layoutBinding.vffv.visibility = View.GONE
                }
            }
        })

        markAttendanceViewModel.renderToast.observe(viewLifecycleOwner, Observer {
         if(it != null && it == "Call Failure") {
             layoutBinding.studentsList.visibility = View.GONE
             layoutBinding.teacherAttendanceProgressBar.visibility = View.GONE
             layoutBinding.emptyOnRackSectionMessageHeading.visibility = View.GONE
             showToast()
         }
        })
        attendanceAdapter.submitList(studentList)
        layoutBinding.markAllPresent.setOnClickListener {
            markAttendanceViewModel.onMarkAllPresentClicked(layoutBinding.markAllPresent.isChecked)

        }
    }

    private fun showToast() {
        Toast.makeText(context!!, "Unable to fetch Employee Data.", Toast.LENGTH_LONG).show()
    }

    private fun initializeAdapter(binding: FragmentMarkTeacherAttendanceBinding) {
        attendanceAdapter = TeacherAttendanceAdapter(
                viewLifecycleOwner,
                markAttendanceViewModel
        )
        binding.studentsList.adapter = attendanceAdapter
    }
}