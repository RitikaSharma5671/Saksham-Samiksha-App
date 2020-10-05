@file:Suppress("UNCHECKED_CAST")

package com.example.student_details.ui.teacher_attendance

import android.annotation.SuppressLint
import android.text.format.DateFormat
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.error.ANError
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.SchoolEmployeesInfo
import com.example.student_details.modules.StudentDataModel
import com.example.student_details.network.BackendCallHelperImpl
import com.example.student_details.ui.teacher_attendance.data.EmployeeInfo
import com.example.student_details.ui.teacher_attendance.data.Employees
import com.hasura.model.SendTeacherAttendanceMutation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.odk.collect.android.application.Collect1
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MarkTeacherAttendanceViewModel : ViewModel() {
    var employeeList: MutableLiveData<List<SchoolEmployeesInfo>> = MutableLiveData()
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
        }
        employeeList.postValue(list)
    }

    @SuppressLint("DefaultLocale")
    fun fetchEmployeeData(schoolCode: String, schoolName: String) {
        isEmployeesListVisible.set(false)
        isEmptyListMessageVisible.set(false)
        isProgressBarVisible.set(true)
        CompositeDisposable().add(BackendCallHelperImpl.getInstance()
                .performLoginApiCall(schoolCode, schoolName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ employeeData: EmployeeInfo ->
                    run {
                        isProgressBarVisible.set(false)
                        if (employeeData.total > 0) {
                            isEmployeesListVisible.set(true)
                            isEmptyListMessageVisible.set(false)
                            fetchEmployeeList(employeeData.userInformation)
                        } else {
                            isEmployeesListVisible.set(false)
                            isEmptyListMessageVisible.set(true)
                            employeeList.postValue(ArrayList())
                        }
                    }

                }, { throwable: Throwable ->
                    run {
                        isEmployeesListVisible.set(false)
                        isEmptyListMessageVisible.set(false)
                        isProgressBarVisible.set(false)
                        employeeList.postValue(ArrayList())
                        renderToast.postValue("Call Failure")
                        if (throwable is ANError) {
                            Timber.d("ve1111ve")
                        } else {
                            Timber.d("131313556ve(((((((ve")
                        }
                    }

                }))
    }

    fun onPrioritySwitchClicked(priorityState: Int, empInfo: SchoolEmployeesInfo) {
        val attendance = priorityState == 1
        val list = employeeList.value!!
        for (employeesInfo in list) {
            if (employeesInfo.employeeId == empInfo.employeeId) {
                employeesInfo.isPresent = attendance
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
            if (employee.temp < 90 && employee.isPresent) {
                flag = false
                break
            }
        }
        return flag
    }

    fun onTemperatureUpdated(user: SchoolEmployeesInfo, ff: String) {
        val temp = ff.toFloat()
        val list = employeeList.value!!
        val lisss = ArrayList<SchoolEmployeesInfo>()
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
        val calendar : Calendar = Calendar.getInstance()
        val currentSelectedDate: String =  DateFormat.format("yyyy-MM-dd",calendar).toString()
        val model = StudentDataModel()
        model.uploadEmployeeAttendanceData(currentSelectedDate, userName, list, object : ApolloQueryResponseListener<SendTeacherAttendanceMutation.Data> {
            override fun onResponseReceived(response: Response<SendTeacherAttendanceMutation.Data>?) {
                try {
                    Collect1.getInstance().analytics.logEvent("teacher_attendance_mark", "teacher_attendance_upload_successful",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                }catch (e:Exception) {
                }
                attendanceUploadSuccessful.postValue("Success")
            }

            override fun onFailureReceived(e: ApolloException?) {
                try {
                    Collect1.getInstance().analytics.logEvent("teacher_attendance_mark", "teacher_attendance_upload_failure",
                            """${userName}_${schoolName}_${schoolCode}_${district}_$block""")
                }catch (e:Exception) {

                }
                attendanceUploadSuccessful.postValue("Failure")
            }

        })
    }

    private fun fetchEmployeeList(userInformation: List<Employees>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        if (realm.schema.contains("SchoolEmployeesInfo"))
            realm.delete(SchoolEmployeesInfo::class.java)
        for (employeeData in userInformation) {
            if (employeeData.data.roleData.designation != "School Head") {
                val schoolEmployeesInfo = SchoolEmployeesInfo(employeeData.username, employeeData.data.accountName,
                        employeeData.data.phone, employeeData.data.roleData.designation, employeeData.data.roleData.schoolCode,
                        employeeData.data.roleData.schoolName, employeeData.data.roleData.district)
                realm.copyToRealmOrUpdate(schoolEmployeesInfo)
            }
        }
        realm.commitTransaction()
        val employees = realm.copyFromRealm(realm
                .where(SchoolEmployeesInfo::class.java).findAll())
        if (employees.size > 0) {
            isEmployeesListVisible.set(true)
            isEmptyListMessageVisible.set(false)
        } else {
            isEmployeesListVisible.set(false)
            isEmptyListMessageVisible.set(true)
        }
        employeeList.postValue(sortEmployeeList(employees))
    }


    fun sortEmployeeList(list: List<SchoolEmployeesInfo>): ArrayList<SchoolEmployeesInfo> {
        Collections.sort(list, SortByName())
        val temp = ArrayList<SchoolEmployeesInfo>()
        temp.addAll(list)
        return temp
    }

}