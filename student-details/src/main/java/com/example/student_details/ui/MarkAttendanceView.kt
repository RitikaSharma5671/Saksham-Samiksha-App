package com.example.student_details.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.student_details.R
import com.example.student_details.databinding.FragmentMarkAttendanceBinding
import com.example.student_details.getViewModelProvider
import com.example.student_details.models.realm.StudentInfo

class MarkAttendanceView : Fragment() {

    private lateinit var layoutBinding: FragmentMarkAttendanceBinding
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val studentList: ArrayList<StudentInfo> = ArrayList()
    private lateinit var mProgress: ProgressDialog
    private val markAttendanceViewModel: MarkAttendanceViewModel by lazy {
        getViewModelProvider(this).get(
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
        markAttendanceViewModel.fetchStudents(arguments)
        layoutBinding.markAllPresent.setOnClickListener {
            markAttendanceViewModel.onMarkAllPresentClicked(layoutBinding.markAllPresent.isChecked)

        }
        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.sending_the_request))
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true
        initializeAdapter(layoutBinding)
        markAttendanceViewModel.filterText.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                layoutBinding.filterName.text = it
            }
        })
        layoutBinding.filterCloseCross.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        markAttendanceViewModel.showIncompleteAlertDialog.observe(viewLifecycleOwner, Observer {
            if (it) {
                SamagraAlertDialog1.Builder(context!!).setTitle("INCOMPLETE DATA").setMessage("Please check there are some incomplete entries for the student list.\n Click Ok to fill the data")
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
             val message =    if(markAttendanceViewModel.highTemp.value!!) {
                 Html.fromHtml("Please ensure that you have entered correct data for the students." + "<font><b>" + "<br/>There are some student(s) with Temperature > 100° F. <br/>Please follow necessary precautions for those student(s)." + "</b></font>" );
                }else{
                 "Please ensure that you have entered correct data for the students."
             }
                SamagraAlertDialog1.Builder(context!!).setTitle("SEND STUDENT DATA").setMessage(message)
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
        markAttendanceViewModel.studentsList.observe(viewLifecycleOwner, Observer {
            val productList = markAttendanceViewModel.studentsList.value
            when {
                productList != null && productList.isNotEmpty() -> {
                    attendanceAdapter.submitList(productList)
                    attendanceAdapter.notifyDataSetChanged()
                }
                else -> {
                    initializeAdapter(layoutBinding)
                }
            }
        })
        attendanceAdapter.submitList(studentList)
    }

    private fun initializeAdapter(binding: FragmentMarkAttendanceBinding) {
        attendanceAdapter = AttendanceAdapter(
                viewLifecycleOwner,
                markAttendanceViewModel
        )
        binding.studentsList.adapter = attendanceAdapter
    }
}
