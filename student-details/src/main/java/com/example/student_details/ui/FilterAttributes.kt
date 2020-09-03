package com.example.student_details.ui

import java.io.Serializable

class FilterAttributes : Serializable{

    var allSections  : ArrayList<String>? =null
            //= arrayOf("A", "B", "C", "D")
    var allGrades   : ArrayList<Int>? =null
            //= arrayOf(1, 2, 3,4,5,6,7,8,9,10,11,12)
    var allStreams   : ArrayList<String>? =null
            // arrayOf("Humanities", "Science", "Commerce", "Vocational")

}
