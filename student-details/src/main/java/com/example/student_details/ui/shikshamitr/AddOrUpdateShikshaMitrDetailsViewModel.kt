package com.example.student_details.ui.shikshamitr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.student_details.contracts.ApolloQueryResponseListener
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.modules.StudentDataModel
import com.hasura.model.SendSMUpdateDataMutation
import com.hasura.model.SendUsageInfoMutation
import com.hasura.model.UpdateShikshaMitraDetailsMutation
import io.realm.Realm
import kotlinx.android.synthetic.main.samiksha_register_shiksha_mitr_screen.*

class AddOrUpdateShikshaMitrDetailsViewModel:ViewModel() {
    val progressBarVisible= MutableLiveData<String>("")
    val showCompleteDialog: MutableLiveData<Boolean> = MutableLiveData(false)

    fun eeded(studentSRN: String, shikshaMitrName: String, shikshaMitrContactNumber: String, shikshaMitrRelation: String,
              shikshaMitrAddress: String, token:String,username:String,schoolCode:String,
              designation:String,  previousName:String,  previousContact:String ) {
         StudentDataModel(token).updateShikshaMitraDetails(studentSRN, shikshaMitrName, shikshaMitrContactNumber, shikshaMitrRelation, shikshaMitrAddress,
                object : ApolloQueryResponseListener<UpdateShikshaMitraDetailsMutation.Data> {
                    override fun onResponseReceived(response: Response<UpdateShikshaMitraDetailsMutation.Data>?) {
                        val realm = Realm.getDefaultInstance()
                        val studentOld: StudentInfo = realm.where(StudentInfo::class.java).equalTo("srn", studentSRN).findFirst()!!
                        val copyOld: StudentInfo = realm.copyFromRealm(studentOld)
                        copyOld.shikshaMitrName = shikshaMitrName
                        copyOld.shikshaMitrContact = shikshaMitrContactNumber
                        copyOld.shikshaMitrRelation = shikshaMitrRelation
                        copyOld.shikshaMitrAddress = shikshaMitrAddress
                        copyOld.isSMRegistered = true
                        realm.beginTransaction()
                        studentOld.deleteFromRealm()
                        realm.insert(copyOld)
                        realm.commitTransaction()
                        sendUsageInfo(token,username,studentSRN,schoolCode,
                                designation,  previousName,  previousContact)
                    }

                    override fun onFailureReceived(e: ApolloException?) {
                        progressBarVisible.postValue("true")
                    }
                })
    }

    private fun sendUsageInfo(token:String,username:String,  srn:String,  schoolCode:String,
                              designation:String,  previousName:String,  previousContact:String ) {
        StudentDataModel(token).updateShikshaMitrUsageInfo( username,  srn,  schoolCode,
                 designation,  previousName,  previousContact, object :ApolloQueryResponseListener<SendSMUpdateDataMutation.Data>{
            override fun onResponseReceived(response: Response<SendSMUpdateDataMutation.Data>?) {
                progressBarVisible.postValue("false")
            }

            override fun onFailureReceived(e: ApolloException?) {
                progressBarVisible.postValue("ffvf")
            }

        })
    }

    fun onClicked() {
         showCompleteDialog.postValue(true)
    }

}
