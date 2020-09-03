package com.example.student_details.ui

import java.io.Serializable

class QueryParameters: Serializable, Cloneable{

    var includeSections: MutableList<String>? = null              // array e.g. ["brand1","brand2"], (optional)
    var includeGrades: MutableList<Int>? = null             // array e.g.["MUL","RED"], (optional)
    var includeStreams: MutableList<String>? = null

}
