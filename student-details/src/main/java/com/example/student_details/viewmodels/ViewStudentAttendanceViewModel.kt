package com.example.student_details.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.modules.StudentDataModel
import com.example.student_details.ui.teacher_aggregate.AttendanceData
import com.hasura.model.FetchAttendanceByGradeSectionQuery
import com.hasura.model.FetchAttendanceByGradeSectionStreamQuery
import java.util.*
import kotlin.collections.ArrayList

@Suppress("SENSELESS_COMPARISON")
class ViewStudentAttendanceViewModel : ViewModel() {

    val toastRender: MutableLiveData<String> = MutableLiveData()
    val attendanceList: MutableLiveData<ArrayList<AttendanceData>> = MutableLiveData()
    val totalStudentCount: MutableLiveData<Int> = MutableLiveData()
    val absentStudents: MutableLiveData<Int> = MutableLiveData()
    val selectedDate = MutableLiveData<String>()
    val selectedDay = MutableLiveData<String>()
    val sundaySelected = MutableLiveData<String>()
    val isProgressBarVisible = ObservableBoolean(false)
    val isStudentListVisible = ObservableBoolean(false)
    val isNoStudentDataMessageVisible = ObservableBoolean(false)
    val isSundayMessageVisible = ObservableBoolean(false)
    val selectedGrade: MutableLiveData<Int> = MutableLiveData(1)
    val selectedSection: MutableLiveData<String> = MutableLiveData("A")
    val selectedStream: MutableLiveData<String> = MutableLiveData("")

    fun fetchRelevantStudentData(currentDay: String, currentSelectedDate: String, schoolCode: String, token:String) {
        if (currentSelectedDate != "") {
            if (currentDay == "Sun") {
                isProgressBarVisible.set(false)
                isStudentListVisible.set(false)
                isSundayMessageVisible.set(true)
                isNoStudentDataMessageVisible.set(false)
                sundaySelected.postValue("Sunday Selected")
            } else {

                if (selectedGrade.value!! < 11) {
                    fetchDataWithoutStreamSelected(currentSelectedDate, schoolCode, token)
                }else {
                    if(selectedStream.value != null && selectedStream.value!!.isEmpty()) {
                        fetchDataWithoutStreamSelected(currentSelectedDate, schoolCode,token)
                    }else if(selectedStream.value != null && selectedStream.value!!.isNotEmpty()){
                        fetchDataWithStreamSelected(currentSelectedDate, schoolCode,token)
                    }else {
                        fetchDataWithoutStreamSelected(currentSelectedDate, schoolCode,token)
                    }
                }
            }
        }

    }

    private fun fetchDataWithoutStreamSelected(currentSelectedDate: String, schoolCode: String, token:String) {
        isProgressBarVisible.set(true)
        isStudentListVisible.set(false)
        isSundayMessageVisible.set(true)
        isNoStudentDataMessageVisible.set(false)
         StudentDataModel(token).fetchAttendanceByGradeSection(currentSelectedDate, selectedGrade.value!!, selectedSection.value!!,
                 object : ApolloQueryResponseListener<FetchAttendanceByGradeSectionQuery.Data> {
                    override fun onResponseReceived(response: Response<FetchAttendanceByGradeSectionQuery.Data>?) {
                        isProgressBarVisible.set(false)
                        if (response!!.data!!.attendance_aggregate().nodes().size > 0) {
                            val count = generateStudentList(response.data!!.attendance_aggregate().nodes())
                            if (count > 0) {
                                isStudentListVisible.set(true)
                                isNoStudentDataMessageVisible.set(false)
                                isSundayMessageVisible.set(false)
                            } else {
                                isStudentListVisible.set(false)
                                isNoStudentDataMessageVisible.set(true)
                                isSundayMessageVisible.set(false)
                            }
                        } else {
                            attendanceList.postValue(ArrayList())
                        }
                    }

                    override fun onFailureReceived(e: ApolloException?) {
                        attendanceList.postValue(ArrayList())
                        toastRender.postValue("Render")
                    }
                })
    }

    private fun fetchDataWithStreamSelected(currentSelectedDate: String, schoolCode: String, token:String) {
        isProgressBarVisible.set(true)
        isStudentListVisible.set(false)
        isSundayMessageVisible.set(true)
        isNoStudentDataMessageVisible.set(false)
        val stream = selectedStream.value!!.substring(0, 1).toUpperCase() + selectedStream.value!!.substring(1).toLowerCase()
         StudentDataModel(token).fetchAttendanceByGradeSectionStream(currentSelectedDate, selectedGrade.value!!, selectedSection.value!!, stream,
                 object : ApolloQueryResponseListener<FetchAttendanceByGradeSectionStreamQuery.Data> {
                    override fun onResponseReceived(response: Response<FetchAttendanceByGradeSectionStreamQuery.Data>?) {
                        isProgressBarVisible.set(false)
                        if (response!!.data!!.attendance_aggregate().nodes().size > 0) {
                            val count = generateStudentListWithStream(response.data!!.attendance_aggregate().nodes())
                            if (count > 0) {
                                isStudentListVisible.set(true)
                                isNoStudentDataMessageVisible.set(false)
                                isSundayMessageVisible.set(false)
                            } else {
                                isStudentListVisible.set(false)
                                isNoStudentDataMessageVisible.set(true)
                                isSundayMessageVisible.set(false)
                            }
                        } else {
                            attendanceList.postValue(ArrayList())
                        }
                    }

                    override fun onFailureReceived(e: ApolloException?) {
                        attendanceList.postValue(ArrayList())
                        toastRender.postValue("Render")
                    }
                })
    }

    private fun generateStudentList(nodes: List<FetchAttendanceByGradeSectionQuery.Node>): Int {
        val totalStudents = nodes.size
        var present = 0
        val list = ArrayList<AttendanceData>()
        for (studentInfo in nodes) {
            val attendanceData = AttendanceData(studentInfo.isPresent, studentInfo.studentByStudent().name(),
                    studentInfo.studentByStudent().grade(), studentInfo.studentByStudent().section(), studentInfo.studentByStudent().srn())
            if (studentInfo.isPresent != null && studentInfo.isPresent!!) present += 1
            list.add(attendanceData)
        }
        totalStudentCount.postValue(totalStudents)
        absentStudents.postValue(totalStudents - present)
        val dd = list
        Collections.sort(dd, SortStudentAttendanceListByName())
        attendanceList.postValue(dd)
        return totalStudents
    }


    private fun generateStudentListWithStream(nodes: List<FetchAttendanceByGradeSectionStreamQuery.Node>): Int {
        val totalStudents = nodes.size
        var present = 0
        val list = ArrayList<AttendanceData>()
        for (studentInfo in nodes) {
            val attendanceData = AttendanceData(studentInfo.isPresent, studentInfo.studentByStudent().name(),
                    studentInfo.studentByStudent().grade(), studentInfo.studentByStudent().section(), studentInfo.studentByStudent().srn())
            if (studentInfo.isPresent != null && studentInfo.isPresent!!) present += 1
            list.add(attendanceData)
        }
        totalStudentCount.postValue(totalStudents)
        absentStudents.postValue(totalStudents - present)
        val dd = list
        Collections.sort(dd, SortStudentAttendanceListByName())
        attendanceList.postValue(dd)
        return totalStudents
    }


    private class SortStudentAttendanceListByName() : Comparator<AttendanceData> {
        /** {@inheritDoc}  */
        override fun compare(o1: AttendanceData, o2: AttendanceData): Int {
            var result: Int = o1.name.compareTo(o2.name)
            if (result == 0) {
                result = o1.srn.compareTo(o2.srn)
            }
            return result
        }
    }


}
