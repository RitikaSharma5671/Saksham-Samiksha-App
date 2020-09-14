package com.example.student_details.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class StudentInfo extends RealmObject {

    @Required
    @PrimaryKey
    private String srn;

    @Required
    private String name;

    private int grade;

    private String section;

    private String stream;

    private String fatherName;

    private String motherName;

    private String fatherContactNumber;

    private boolean isPresent;

    private float temp;
    @Required
    private String school_code;

    public StudentInfo() {
    }
    public StudentInfo(String srn, String name, int grade, String section, String stream,
                       String fatherName, String motherName, String fatherContactNumber, String school_code) {
        this.srn = srn;
        this.name = name;
        this.grade = grade;
        this.section = section;
        this.stream = stream;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.fatherContactNumber = fatherContactNumber;
        this.school_code = school_code;
        this.isPresent = false;
        this.temp = 0.0f;
    }

    public String getSrn() {
        return srn;
    }

    public void setSrn(String srn) {
        this.srn = srn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherContactNumber() {
        return fatherContactNumber;
    }

    public void setFatherContactNumber(String fatherContactNumber) {
        this.fatherContactNumber = fatherContactNumber;
    }

    public String getSchoolCode() {
        return school_code;
    }

    public void setSchoolCode(String schoolCode) {
        this.school_code = schoolCode;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }
}
