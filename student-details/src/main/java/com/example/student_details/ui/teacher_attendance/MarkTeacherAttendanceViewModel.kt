@file:Suppress("UNCHECKED_CAST")

package com.example.student_details.ui.teacher_attendance

import android.annotation.SuppressLint
import android.text.format.DateFormat
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.SchoolEmployeesAttendanceData
import com.example.student_details.models.realm.SchoolEmployeesInfo
import com.example.student_details.modules.StudentDataModel
import com.hasura.model.SendTeacherAttendanceNewFormatMutation
import io.realm.Realm
import org.odk.collect.android.application.Collect1
import java.util.*
import kotlin.collections.ArrayList

class MarkTeacherAttendanceViewModel : ViewModel() {
    var employeeList: MutableLiveData<List<SchoolEmployeesAttendanceData>> = MutableLiveData()
    val renderToast: MutableLiveData<String> = MutableLiveData()
    val isEmployeesListVisible = ObservableBoolean(false)
    val isEmptyListMessageVisible = ObservableBoolean(false)
    val isProgressBarVisible = ObservableBoolean(false)
    val showIncompleteAlertDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val showCompleteDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val attendanceUploadSuccessful: MutableLiveData<String> = MutableLiveData("false")

    fun onMarkAllPresentClicked(checked: Boolean) {
        val list = employeeList.value!!
        for (employeeData in list) {
            employeeData.isPresent = checked
            if(checked) {
                employeeData.attendanceStatus = "Present in School"
            }else {
                employeeData.attendanceStatus = "Mark Attendance"
            }
        }
        employeeList.postValue(list)
    }

    @SuppressLint("DefaultLocale")
    fun fetchEmployeeData(schoolCode: String, schoolName: String) {
        isEmployeesListVisible.set(false)
        isEmptyListMessageVisible.set(false)
        isProgressBarVisible.set(true)
        fetchEmployeeList()
    }
//        CompositeDisposable().add(BackendCallHelperImpl.getInstance()
//                .performLoginApiCall(schoolCode, schoolName)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ employeeData: EmployeeInfo ->
//                    run {
//                        isProgressBarVisible.set(false)
//                        if (employeeData.total > 0) {
//                            isEmployeesListVisible.set(true)
//                            isEmptyListMessageVisible.set(false)
//                        } else {
//                            isEmployeesListVisible.set(false)
//                            isEmptyListMessageVisible.set(true)
//                            employeeList.postValue(ArrayList())
//                        }
//                    }
//
//                }, { throwable: Throwable ->
//                    run {
//                        isEmployeesListVisible.set(false)
//                        isEmptyListMessageVisible.set(false)
//                        isProgressBarVisible.set(false)
//                        employeeList.postValue(ArrayList())
//                        renderToast.postValue("Call Failure")
//                        if (throwable is ANError) {
//                            Timber.d("ve1111ve")
//                        } else {
//                            Timber.d("131313556ve(((((((ve")
//                        }
//                    }
//
//                }))
//    }

    fun onPrioritySwitchClicked(priorityState: String, empInfo: SchoolEmployeesAttendanceData) {
        val list = employeeList.value!!
        for (employeesInfo in list) {
            if (employeesInfo.employeeId == empInfo.employeeId) {
                employeesInfo.isPresent = priorityState == "Present in School"
                employeesInfo.attendanceStatus = priorityState
                break
            }
        }
        employeeList.postValue(list)
    }

    fun onOtherReasonChanged(priorityState: String, empInfo: SchoolEmployeesAttendanceData) {
        val list = employeeList.value!!
        for (employeesInfo in list) {
            if (employeesInfo.employeeId == empInfo.employeeId) {
                employeesInfo.otherReason = priorityState
                break
            }
        }
        employeeList.postValue(list)
    }

    fun onSendAttendanceClicked() {
        if (checkIfAllDataFilled()) {
            showCompleteDialog.postValue(true)
        } else {
            showIncompleteAlertDialog.postValue(true)
        }
    }

    private fun checkIfAllDataFilled(): Boolean {
        var flag = true
        val list = employeeList.value!!
        for (employee in list) {
            if (employee.temp < 90 || employee.attendanceStatus == "") {
                flag = false
                break
            }
        }
        return flag
    }

    fun onTemperatureUpdated(user: SchoolEmployeesAttendanceData, ff: String) {
        val temp = ff.toFloat()
        val list = employeeList.value!!
        val lisss = ArrayList<SchoolEmployeesAttendanceData>()
        for (employees in list) {
            if (employees.employeeId == user.employeeId) {
                val ddd = employees
                ddd.temp = temp
                lisss.add(ddd)
            } else {
                lisss.add(employees)
            }
        }
        employeeList.postValue(lisss)
    }

    fun uploadAttendanceData(userName: String, schoolCode: String, schoolName: String, district: String, block: String) {
        val list = employeeList.value!!
        val calendar: Calendar = Calendar.getInstance()
        val currentSelectedDate: String = DateFormat.format("yyyy-MM-dd", calendar).toString()
        val model = StudentDataModel()
        model.uploadEmployeeAttendanceData(currentSelectedDate, userName, list, object : ApolloQueryResponseListener<SendTeacherAttendanceNewFormatMutation.Data> {
            override fun onResponseReceived(response: Response<SendTeacherAttendanceNewFormatMutation.Data>?) {
                try {
                    Collect1.getInstance().analytics.logEvent("teacher_attendance_mark", "teacher_attendance_upload_successful",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                } catch (e: Exception) {
                }
                attendanceUploadSuccessful.postValue("Success")
            }

            override fun onFailureReceived(e: ApolloException?) {
                try {
                    Collect1.getInstance().analytics.logEvent("teacher_attendance_mark", "teacher_attendance_upload_failure",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                } catch (e: Exception) {

                }
                attendanceUploadSuccessful.postValue("Failure")
            }

        })
    }

    private fun fetchEmployeeList() {
        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        if (realm.schema.contains("SchoolEmployeesInfo"))
//            realm.delete(SchoolEmployeesInfo::class.java)
//        for (employeeData in userInformation) {
//            if (employeeData.data.roleData.designation != "School Head") {
//                val schoolEmployeesInfo = SchoolEmployeesInfo(employeeData.username, employeeData.data.accountName,
//                        employeeData.data.phone, employeeData.data.roleData.designation, employeeData.data.roleData.schoolCode,
//                        employeeData.data.roleData.schoolName, employeeData.data.roleData.district)
//                realm.copyToRealmOrUpdate(schoolEmployeesInfo)
//            }
//        }
//        realm.commitTransaction()
        val employees = realm.copyFromRealm(realm
                .where(SchoolEmployeesInfo::class.java).findAll())
        if (employees != null && employees.size > 0) {
            val finalList = convertToLocal(employees)
            isEmployeesListVisible.set(true)
            isProgressBarVisible.set(false)
            isEmptyListMessageVisible.set(false)
            employeeList.postValue(sortEmployeeList(finalList))
        } else {
            employeeList.postValue(ArrayList())
            isEmployeesListVisible.set(false)
            isProgressBarVisible.set(false)
            isEmptyListMessageVisible.set(true)
        }
    }

    private fun convertToLocal(employees: List<SchoolEmployeesInfo>): List<SchoolEmployeesAttendanceData> {
        val list = ArrayList<SchoolEmployeesAttendanceData>()
        for (employeeData in employees) {
            val temp = SchoolEmployeesAttendanceData(employeeData.employeeId, employeeData.name,
                    employeeData.contactNumber, employeeData.designation, employeeData.schoolCode,
                    employeeData.schoolName, employeeData.district)
            list.add(temp)
        }
        return list
    }


    private fun sortEmployeeList(list: List<SchoolEmployeesAttendanceData>): ArrayList<SchoolEmployeesAttendanceData> {
        Collections.sort(list, SortByName())
        val temp = ArrayList<SchoolEmployeesAttendanceData>()
        temp.addAll(list)
        return temp
    }

}