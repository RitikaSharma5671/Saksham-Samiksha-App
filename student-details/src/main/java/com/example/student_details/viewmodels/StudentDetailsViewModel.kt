package com.example.student_details.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.modules.StudentDataModel
import com.hasura.model.UpdateStudentSectionMutation
import io.realm.Realm


class StudentDetailsViewModel(private val applicationValue: Application, private val ss: String) :
        AndroidViewModel(applicationValue) {

    var studentsList: MutableLiveData<List<StudentInfo>> = MutableLiveData()
    val selectedGrade: MutableLiveData<Int> = MutableLiveData(0)
    val selectedSection: MutableLiveData<String> = MutableLiveData("")
    val selectedStream: MutableLiveData<String> = MutableLiveData("")
    val isStudentListVisible = ObservableBoolean(false)
    val isEmptyListMessageVisible = ObservableBoolean(false)
    val progressBarVisible : MutableLiveData<String> = MutableLiveData("")

    val masterStudentList = MutableLiveData<ArrayList<StudentInfo>>()
//    var garmentsList: LiveData<MutableList<ClosetResponseData.ClosetProductState>> =
//            Transformations.map(atHomeSectionData) { closetAPIResponse -> getProcessedGarmentList(closetAPIResponse) }


    fun onApplyFiltersClicked() {
        val realm = Realm.getDefaultInstance()
        val selectedGrade = selectedGrade.value!!
        val selectedSection = selectedSection.value!!
        val selectedStream = selectedStream.value!!
        val studentList: ArrayList<StudentInfo> = ArrayList()
        if (selectedGrade < 11 || selectedStream == "") {
            val sts = realm.copyFromRealm(realm
                    .where(StudentInfo::class.java)
                    .equalTo("section", selectedSection).equalTo("grade", selectedGrade).findAll())
//                        val task: List<StudentInfo> = realm.where(StudentInfo::class.java).equalTo("section", section).equalTo("grade", jjj).findAll()
            if (sts.isNotEmpty()) studentList.addAll(sts)
        } else {
            val stream = selectedStream.substring(0, 1).toUpperCase() + selectedStream.substring(1).toLowerCase()
            val task = realm.copyFromRealm(realm
                    .where(StudentInfo::class.java)
                    .equalTo("stream", stream).equalTo("section", selectedSection)
                    .equalTo("grade", selectedGrade).findAll())
            if (task.isNotEmpty()) studentList.addAll(task)
        }
        if (studentList.size > 0) {
            isStudentListVisible.set(true)
            isEmptyListMessageVisible.set(false)
        } else {
            isStudentListVisible.set(false)
            isEmptyListMessageVisible.set(true)
        }
        studentsList.postValue(studentList.toList())
    }

    fun fetchStudentData() {


    }

    fun onSectionEdited(studentData: StudentInfo, changedValue: String,token:String) {
        progressBarVisible.postValue("true")
        val model =  StudentDataModel(token)
        model.updateStudentSection(studentData.srn, changedValue, object : ApolloQueryResponseListener<UpdateStudentSectionMutation.Data> {
            override fun onResponseReceived(response: Response<UpdateStudentSectionMutation.Data>?) {
                val realm = Realm.getDefaultInstance()
                val studentOld: StudentInfo = realm.where(StudentInfo::class.java).equalTo("srn", studentData.srn).findFirst()!!
                val copyOld: StudentInfo = realm.copyFromRealm(studentOld)
                copyOld.section = changedValue
                realm.beginTransaction()
                studentOld.deleteFromRealm()
                realm.copyToRealmOrUpdate(copyOld)
                realm.commitTransaction()
                onApplyFiltersClicked()
                progressBarVisible.postValue("false")
            }

            override fun onFailureReceived(e: ApolloException?) {
//                attendanceUploadSuccessful.postValue("Failure")

            }
        })


    }
}
