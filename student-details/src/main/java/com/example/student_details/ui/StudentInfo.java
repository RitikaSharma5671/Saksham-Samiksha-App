package com.example.student_details.ui;

import com.google.gson.Gson;

import java.util.ArrayList;

public class StudentInfo {
        String srn;

        String studentName;

        int standard;

        String stream;

        String section ;

        String fatherName;

//        @SerializedName("fatherContactNumber")
//        val fatherContactNumber: String;

        String motherName;

    public String getSrn() {
        return srn;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getStandard() {
        return standard;
    }

    public String getStream() {
        return stream;
    }

    public String getSection() {
        return section;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

//
//        @SerializedName("motherContactNumber")
//        val motherContactNumber: String
//)

    }
