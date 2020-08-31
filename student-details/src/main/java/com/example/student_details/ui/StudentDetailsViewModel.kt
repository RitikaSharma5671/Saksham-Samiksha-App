package com.example.student_details.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper


class StudentDetailsViewModel(private val applicationValue: Application, private val ss: String) :
        AndroidViewModel(applicationValue) {

//    val atHomeSectionData = MutableLiveData<POJOWrapper<ClosetResponseData>>()
//    var garmentsList: LiveData<MutableList<ClosetResponseData.ClosetProductState>> =
//            Transformations.map(atHomeSectionData) { closetAPIResponse -> getProcessedGarmentList(closetAPIResponse) }

    fun fetchStudentData() : ArrayList<StudentInfo> {
        val student = ArrayList<StudentInfo>()
        val sfff = "[\n" +
                "  {\n" +
                "    \"srn\": 1705386660,\n" +
                "    \"studentName\": \"MANISHA\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 12,\n" +
                "    \"stream\": \"Science\",\n" +
                "    \"fatherName\": \"CHANDER SHEKHAR\",\n" +
                "    \"motherName\": \"MANJU DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1705399187,\n" +
                "    \"studentName\": \"AMIT\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 12,\n" +
                "    \"stream\": \"Science\",\n" +
                "    \"fatherName\": \"JAIBHAGWAN\",\n" +
                "    \"motherName\": \"KAMLESH\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1608739072,\n" +
                "    \"studentName\": \"RITIK\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 12,\n" +
                "    \"stream\": \"Arts\",\n" +
                "    \"fatherName\": \"GULAB SINGH\",\n" +
                "    \"motherName\": \"SUNIL DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1515964879,\n" +
                "    \"studentName\": \"MANVI  GAUTAM\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 11,\n" +
                "    \"stream\": \"Science\",\n" +
                "    \"fatherName\": \"RAMMEHAR\",\n" +
                "    \"motherName\": \"RAJBALA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1613817745,\n" +
                "    \"studentName\": \"VICKY KUMAR\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 11,\n" +
                "    \"stream\": \"Commerce\",\n" +
                "    \"fatherName\": \"KARANVEER\",\n" +
                "    \"motherName\": \"SAROJ DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1613819088,\n" +
                "    \"studentName\": \"RACHIT YADAV\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 10,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"DEVENDER KUMAR\",\n" +
                "    \"motherName\": \"BEENA DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1613859367,\n" +
                "    \"studentName\": \"PRAVEEN\",\n" +
                "    \"section\": \"B\",\n" +
                "    \"standard\": 9,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"RAVINDER\",\n" +
                "    \"motherName\": \"REENA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1612793036,\n" +
                "    \"studentName\": \"SAHIL SINGH\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 9,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"PRATAP SINGH\",\n" +
                "    \"motherName\": \"LAXMI DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1612960572,\n" +
                "    \"studentName\": \"JAY DAYMA\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 8,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"MAHESH CHAND\",\n" +
                "    \"motherName\": \"SURESH DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1612962054,\n" +
                "    \"studentName\": \"GARIMA\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 8,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"SANJAY KUMAR\",\n" +
                "    \"motherName\": \"SUNITA DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1613010875,\n" +
                "    \"studentName\": \"ASHISH YADAV\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 7,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"SUMER SINGH\",\n" +
                "    \"motherName\": \"SUNITA DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1613013618,\n" +
                "    \"studentName\": \"GULKANDI\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 7,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"RINKU\",\n" +
                "    \"motherName\": \"RAJNI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1807384280,\n" +
                "    \"studentName\": \"VISHAL\",\n" +
                "    \"section\": \"B\",\n" +
                "    \"standard\": 6,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"SAMARJEET\",\n" +
                "    \"motherName\": \"SEEMA DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1707933077,\n" +
                "    \"studentName\": \"VIVEK RAJ\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 6,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"PARDEEP KUMAR\",\n" +
                "    \"motherName\": \"PARMJEET\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1709987903,\n" +
                "    \"studentName\": \"KANCHAN\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 5,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"ASHOK\",\n" +
                "    \"motherName\": \"PARVEEN\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1711423242,\n" +
                "    \"studentName\": \"UTKARSH\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 4,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"SANJAY KUMAR\",\n" +
                "    \"motherName\": \"MAMTA DEVI\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1713594458,\n" +
                "    \"studentName\": \"ADITYABIR SINGH\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 3,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"RAVINDER SINGH\",\n" +
                "    \"motherName\": \"GURPREET KAUR\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1713966640,\n" +
                "    \"studentName\": \"ANANYA SINGH\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 2,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"VIJAI PRATAP SINGH\",\n" +
                "    \"motherName\": \"MRIGANKA SINGH\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1715337936,\n" +
                "    \"studentName\": \"BHUMIKA YADAV\",\n" +
                "    \"section\": \"B\",\n" +
                "    \"standard\": 2,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"VIKRAM YADAV\",\n" +
                "    \"motherName\": \"KAMLESH YADAV\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1715437911,\n" +
                "    \"studentName\": \"BHUMIKA YADAV\",\n" +
                "    \"section\": \"B\",\n" +
                "    \"standard\": 2,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"VIKRAM YADAV\",\n" +
                "    \"motherName\": \"KAMLESH YADAV\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"srn\": 1715338074,\n" +
                "    \"studentName\": \"KASHISH KESARWANI\",\n" +
                "    \"section\": \"A\",\n" +
                "    \"standard\": 1,\n" +
                "    \"stream\": \"\",\n" +
                "    \"fatherName\": \"VIVEK KUMAR KESARWANI\",\n" +
                "    \"motherName\": \"JYOTHI KESARWANI\"\n" +
                "  }\n" +
                "]"
        val lll = ObjectMapper().readValue<ArrayList<StudentInfo>>(sfff, object : TypeReference<List<StudentInfo>>() {})
        Log.d("RFevceerfv %s", lll.size.toString())
        return lll
    }

}
