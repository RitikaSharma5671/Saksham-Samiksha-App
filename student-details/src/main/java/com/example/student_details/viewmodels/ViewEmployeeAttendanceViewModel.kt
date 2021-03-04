package com.example.student_details.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.modules.StudentDataModel
import com.example.student_details.ui.employee_aggregate.EmpAttendanceData
import com.hasura.model.FetchTeacherAttendanceQuery

//import com.hasura.model.FetchTeacherAttendanceQuery

@Suppress("SENSELESS_COMPARISON")
class ViewEmployeeAttendanceViewModel : ViewModel() {

    val toastRender: MutableLiveData<String> = MutableLiveData()
    val attendanceList: MutableLiveData<ArrayList<EmpAttendanceData>> = MutableLiveData()
    val totalStudentCount: MutableLiveData<Int> = MutableLiveData()
    val presentInSchoolCount: MutableLiveData<Int> = MutableLiveData()
    val selectedDate = MutableLiveData<String>()
    val selectedDay = MutableLiveData<String>()
    val sundaySelected = MutableLiveData<String>()
    val isProgressBarVisible = MutableLiveData<Boolean>()
    val isStudentListVisible = MutableLiveData<Boolean>()
    val isNoStudentDataMessageVisible = MutableLiveData<Boolean>()
    val isSundayMessageVisible = MutableLiveData<Boolean>()

    fun fetchRelevantStudentData(currentDay: String, currentSelectedDate: String, schoolCode: String, token:String) {
        if (currentSelectedDate != "") {
            if (currentDay == "Sun") {
                isProgressBarVisible.postValue(false)
                isStudentListVisible.postValue(false)
                isSundayMessageVisible.postValue(true)
                isNoStudentDataMessageVisible.postValue(false)
                sundaySelected.postValue("Sunday Selected")
            } else {
                fetchAttendanceForSchool(currentSelectedDate, schoolCode,token)
            }
        }

    }

    private fun fetchAttendanceForSchool(currentSelectedDate: String, schoolCode: String, token:String) {
        isProgressBarVisible.postValue(true)
        isStudentListVisible.postValue(false)
        isSundayMessageVisible.postValue(false)
        isNoStudentDataMessageVisible.postValue(false)
          StudentDataModel(token).fetchEmployeeAttendanceForSchool(currentSelectedDate, schoolCode,
                object : ApolloQueryResponseListener<FetchTeacherAttendanceQuery.Data> {
                    override fun onResponseReceived(response: Response<FetchTeacherAttendanceQuery.Data>?) {
                        isProgressBarVisible.postValue(false)
                        if (response?.data != null && response.data?.teacher_attendance_updated()!! != null &&
                                response.data?.teacher_attendance_updated()!!.size > 0) {
                            val count = generateEmployeeList(response.data!!.teacher_attendance_updated(),schoolCode)
                            if (count > 0) {
                                isStudentListVisible.postValue(true)
                                isNoStudentDataMessageVisible.postValue(false)
                                isSundayMessageVisible.postValue(false)
                            } else {
                                isStudentListVisible.postValue(false)
                                isNoStudentDataMessageVisible.postValue(true)
                                isSundayMessageVisible.postValue(false)
                            }
                        } else {
                            isStudentListVisible.postValue(false)
                            isNoStudentDataMessageVisible.postValue(true)
                            isSundayMessageVisible.postValue(false)
//                            toastRender.postValue("Render")
                            attendanceList.postValue(ArrayList())
                        }
                    }

                    override fun onFailureReceived(e: ApolloException?) {
                        attendanceList.postValue(ArrayList())
                        toastRender.postValue("Render")
                    }
                })
    }

    private fun generateEmployeeList(nodes: List<FetchTeacherAttendanceQuery.Teacher_attendance_updated>,  schoolCode: String): Int {
        var present = 0
        val list = ArrayList<EmpAttendanceData>()
        for (studentInfo in nodes) {
            val attendanceData = EmpAttendanceData(studentInfo.isPresentInSchool, studentInfo.employee_name(),
                    studentInfo.employee_id(), studentInfo.employee_designation(), schoolCode,
                    if (studentInfo.attendance_status() != null) studentInfo.attendance_status() else "-",
                    if (studentInfo.other_reason() != null) studentInfo.other_reason() else "-")
            if (studentInfo.isPresentInSchool != null && studentInfo.isPresentInSchool) present += 1
            list.add(attendanceData)
        }
        totalStudentCount.postValue(nodes.size)
        presentInSchoolCount.postValue(present)
        attendanceList.postValue(list)
        return nodes.size
    }


}
