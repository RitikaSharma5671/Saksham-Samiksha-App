@file:Suppress("UNCHECKED_CAST")

package com.example.student_details.viewmodels

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.modules.StudentDataModel
import com.hasura.model.SendAttendanceMutation
import io.realm.Realm
import org.odk.collect.android.application.Collect1
import java.util.*
import kotlin.collections.ArrayList

class MarkStudentAttendanceViewModel : ViewModel() {
    var filterText: MutableLiveData<String> = MutableLiveData()
    var studentsList: MutableLiveData<List<StudentInfo>> = MutableLiveData()
    val isStudentListVisible = ObservableBoolean(false)
    val isEmptyListMessageVisible = ObservableBoolean(false)
    val showIncompleteAlertDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val showCompleteDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val highTemp: MutableLiveData<Boolean> = MutableLiveData(false)
    val attendanceUploadSuccessful: MutableLiveData<String> = MutableLiveData("false")
    val selectedGrades: MutableLiveData<ArrayList<Int>> = MutableLiveData()
    var selectedSections: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var selectedStreams: MutableLiveData<ArrayList<String>> = MutableLiveData()
    fun onMarkAllPresentClicked(checked: Boolean) {
        val list = studentsList.value!!
        for (student in list) {
            student.isPresent = checked
        }
        studentsList.postValue(list)
    }

    @SuppressLint("DefaultLocale")
    fun fetchStudents(arguments: Bundle?) {
        isStudentListVisible.set(false)
        isEmptyListMessageVisible.set(false)
        val studentList: ArrayList<StudentInfo> = ArrayList()
        val selectedGradesArgument = arguments!!.getSerializable("selectedGrades") as ArrayList<Int>
        selectedGrades.postValue(selectedGradesArgument)
        var selectedSectionsArgument: ArrayList<String> = ArrayList()
        var selectedStreamsArgument: ArrayList<String> = ArrayList()
        if (arguments.getSerializable("selectedSections") != null) {
            selectedSectionsArgument = arguments.getSerializable("selectedSections") as ArrayList<String>
            selectedSections.postValue(selectedSectionsArgument)
        }

        if (arguments.getSerializable("selectedStreams") != null) {
            selectedStreamsArgument = arguments.getSerializable("selectedStreams") as ArrayList<String>
            selectedStreams.postValue(selectedStreamsArgument)

        }

        makeFilterScreenTextVisible(selectedGradesArgument, selectedSectionsArgument, selectedStreamsArgument)
        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
        for (jjj in selectedGradesArgument) {
            if (jjj < 11 || selectedStreamsArgument.size <= 0) {
                if (selectedSectionsArgument.size != 0) {
                    for (section in selectedSectionsArgument) {
                        val sts = realm.copyFromRealm(realm
                                .where(StudentInfo::class.java)
                                .equalTo("section", section).equalTo("grade", jjj).findAll())
                        if (sts.isNotEmpty()) studentList.addAll(sts)
                    }
                } else {
                    val task = realm.copyFromRealm(realm
                            .where(StudentInfo::class.java)
                            .equalTo("grade", jjj).findAll())
                    if (task.isNotEmpty()) studentList.addAll(task)
                }
            } else {
                if (selectedSectionsArgument.size != 0) {
                    for (section in selectedSectionsArgument) {
                        for (streams in selectedStreamsArgument) {
                            val stream = streams.substring(0, 1).toUpperCase() + streams.substring(1).toLowerCase()
                            val task = realm.copyFromRealm(realm
                                    .where(StudentInfo::class.java)
                                    .equalTo("stream", stream).equalTo("section", section)
                                    .equalTo("grade", jjj).findAll())
                            if (task.isNotEmpty()) studentList.addAll(task)
                        }

                    }
                } else {
                    for (streams in selectedStreamsArgument) {
                        val stream = streams.substring(0, 1).toUpperCase() + streams.substring(1).toLowerCase()
                        val task = realm.copyFromRealm(realm
                                .where(StudentInfo::class.java)
                                .equalTo("stream", stream)
                                .equalTo("grade", jjj).findAll())
                        if (task.isNotEmpty()) studentList.addAll(task)
                    }
                }
            }
        }
//        realm.commitTransaction()
        if (studentList.size > 0) {
            isStudentListVisible.set(true)
            isEmptyListMessageVisible.set(false)
        } else {
            isStudentListVisible.set(false)
            isEmptyListMessageVisible.set(true)
        }
        studentsList.postValue(studentList.toList())
    }

    private fun makeFilterScreenTextVisible(selectedGrades: ArrayList<Int>, selectedSections: ArrayList<String>,
                                            selectedStreams: ArrayList<String>) {
        var grades = "["
        var sections = "["
        var streams = "["
        val lastGrade = selectedGrades[selectedGrades.size - 1]
        for (grade in selectedGrades) {
            grades = if (grade == lastGrade)
                "$grades$grade]"
            else
                "$grades$grade, "
        }
        if (selectedSections.size > 0) {
            val lastSection = selectedSections[selectedSections.size - 1]
            for (section in selectedSections) {
                sections = if (section == lastSection)
                    "$sections$section]"
                else
                    "$sections$section, "
            }
        }
        if (selectedStreams.size > 0) {
            val lastStream = selectedStreams[selectedStreams.size - 1]
            for (stream in selectedStreams) {
                streams = if (stream == lastStream)
                    "$streams$stream]"
                else
                    "$streams$stream, "
            }
        }
        var text = "You are viewing students with these filters:\n"
        text = if (selectedSections.size > 0 && selectedStreams.size > 0) {
            "$text Grades - $grades, Sections - $sections, Streams - $streams"
        } else if (selectedSections.size > 0) {
            "$text Grades - $grades and Sections - $sections"
        } else if (selectedStreams.size > 0) {
            "$text Grades - $grades and Streams - $streams"
        } else {
            "$text Grades - $grades"
        }
        filterText.postValue(text)
    }

    fun onPrioritySwitchClicked(priorityState: Int, studentInfo: StudentInfo) {
        val attendance = priorityState == 1
        val list = studentsList.value!!
        for (students in list) {
            if (students.srn == studentInfo.srn) {
                students.isPresent = attendance
                break
            }
        }
        studentsList.postValue(list)
    }

    fun onSendAttendanceClicked() {
        if (checkIfAllDataFilled()) {
            val list = studentsList.value!!
            for (student in list) {
                if (student.temp > 100) {
                    highTemp.postValue(true)
                }
            }
            showCompleteDialog.postValue(true)
        } else {
            showIncompleteAlertDialog.postValue(true)
        }
    }

    private fun checkIfAllDataFilled(): Boolean {
        var flag = true
        val list = studentsList.value!!
        for (student in list) {
            if (student.temp < 90 && student.isPresent) {
                flag = false
                break
            }
        }
        return flag
    }

    fun onTemperatureUpdated(studentInfo: StudentInfo, ff: String) {
        val temp = ff.toFloat()
        val list = studentsList.value!!
        val lisss = ArrayList<StudentInfo>()
        for (students in list) {
            if (students.srn == studentInfo.srn) {
                val ddd = students
                ddd.temp = temp
                lisss.add(ddd)
            } else {
                lisss.add(students)
            }
        }
        studentsList.postValue(lisss)
    }

    fun uploadAttendanceData(userName: String, schoolName: String, schoolCode: String, district: String, block: String) {
        val list = studentsList.value!!
        val model = StudentDataModel()
        val calendar: Calendar = Calendar.getInstance()
        val currentSelectedDate: String = DateFormat.format("yyyy-MM-dd", calendar).toString()
        model.uploadAttendanceData(currentSelectedDate, userName, list, object : ApolloQueryResponseListener<SendAttendanceMutation.Data> {
            override fun onResponseReceived(response: Response<SendAttendanceMutation.Data>?) {
                try {
                    Collect1.getInstance().analytics.logEvent("student_attendance_mark", "student_attendance_upload_successful",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                } catch (e: Exception) {

                }
                attendanceUploadSuccessful.postValue("Success")
            }

            override fun onFailureReceived(e: ApolloException?) {
                try {
                    Collect1.getInstance().analytics.logEvent("student_attendance_mark", "student_attendance_upload_failure",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                } catch (e: Exception) {

                }
                attendanceUploadSuccessful.postValue("Failure")
            }

        })
    }
}