package com.example.student_details.ui.shikshamitr.ui

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.ui.shikshamitr.SortSMListByName
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList

class ShikshaMitrDetailsViewModel : ViewModel() {
    val launchSM: MutableLiveData<StudentInfo> = MutableLiveData()
    val shikshaMitraRegistered: MutableLiveData<Int> = MutableLiveData(0)
    val totalStudentCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedStream: MutableLiveData<String> = MutableLiveData("")
    val selectedGrade: MutableLiveData<Int> = MutableLiveData(0)
    val selectedSection: MutableLiveData<String> = MutableLiveData("")
    val isStudentListVisible = ObservableBoolean(false)
    val isEmptyListMessageVisible = ObservableBoolean(false)
    val progressBarVisible: MutableLiveData<String> = MutableLiveData("")
    var studentsList: MutableLiveData<List<StudentInfo>> = MutableLiveData()

    fun onApplyFiltersClicked(selectedGrade: Int, selectedSection: String, selectedStream: String) {
        val realm = Realm.getDefaultInstance()
        val studentList: ArrayList<StudentInfo> = ArrayList()
        if (selectedGrade < 11) {
            if (selectedSection.isEmpty() || selectedSection == "Section") {
                val sts = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("grade", selectedGrade).findAll())
                if (sts.isNotEmpty()) studentList.addAll(sts)
            } else {
                val sts = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("section", selectedSection).equalTo("grade", selectedGrade).findAll())
                if (sts.isNotEmpty()) studentList.addAll(sts)
            }

        } else {
            if ((selectedSection == "" || selectedSection == "Section") && selectedStream == "") {
                val sts = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("grade", selectedGrade).findAll())
                if (sts.isNotEmpty()) studentList.addAll(sts)
            } else if (selectedSection != "" && selectedSection != "Section" && selectedStream == "") {
                val studentListFromDB = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("section", selectedSection)
                        .equalTo("grade", selectedGrade).findAll())
                if (studentListFromDB.isNotEmpty()) studentList.addAll(studentListFromDB)
            } else if ((selectedSection == "" || selectedSection == "Section") && selectedStream != "") {
                val studentListFromDB = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("stream", selectedStream)
                        .equalTo("grade", selectedGrade).findAll())
                if (studentListFromDB.isNotEmpty()) studentList.addAll(studentListFromDB)
            } else {
                val studentListFromDB = realm.copyFromRealm(realm
                        .where(StudentInfo::class.java)
                        .equalTo("stream", selectedStream).equalTo("section", selectedSection)
                        .equalTo("grade", selectedGrade).findAll())
                if (studentListFromDB.isNotEmpty()) studentList.addAll(studentListFromDB)
            }
        }
        if (studentList.size > 0) {
            isStudentListVisible.set(true)
            isEmptyListMessageVisible.set(false)
        } else {
            isStudentListVisible.set(false)
            isEmptyListMessageVisible.set(true)
        }
        totalStudentCount.postValue(studentList.size)
        shikshaMitraRegistered.postValue(findRegisteredStudents(studentList.toList()))

        if (studentList.size == 0) {
            studentsList.postValue(studentList.toList())
        } else {
            studentsList.postValue(sortByNameAndRegistration(studentList.toList()))
        }
    }

    private fun sortByNameAndRegistration(toList: List<StudentInfo>): List<StudentInfo> {

        val registeredList = ArrayList<StudentInfo>()
        val unRegisteredList = ArrayList<StudentInfo>()
        for (element in toList) {
            if (element.isSMRegistered) registeredList.add(element)
            else unRegisteredList.add(element)
        }
        Collections.sort(registeredList, SortSMListByName())
        Collections.sort(unRegisteredList, SortSMListByName())
        val finalList  = ArrayList<StudentInfo>()
        finalList.addAll(unRegisteredList)
        finalList.addAll(registeredList)
        return finalList.toList()
    }

    private fun findRegisteredStudents(toList: List<StudentInfo>): Int {
        if (toList.isEmpty()) return 0
        var count = 0
        for (student in toList) {
            if (student.isSMRegistered) count += 1
        }
        return count
    }

    fun findInitialRegisterations() {
        val realm = Realm.getDefaultInstance()
        val sts = realm.copyFromRealm(realm
                .where(StudentInfo::class.java)
                .findAll())
        totalStudentCount.postValue(sts.size)
        shikshaMitraRegistered.postValue(findRegisteredStudents(sts.toList()))
    }



}
