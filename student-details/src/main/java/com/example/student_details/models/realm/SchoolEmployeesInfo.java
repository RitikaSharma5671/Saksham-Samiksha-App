package com.example.student_details.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class SchoolEmployeesInfo extends RealmObject {

    @Required
    @PrimaryKey
    private String employeeId;

    @Required
    private String name;

    private String designation;

    private String contactNumber;
    private String district;

    @Required
    private String schoolCode;

    private String schoolName;

    private boolean isPresent;

    private float temp;


    public SchoolEmployeesInfo() {
    }
    public SchoolEmployeesInfo(String employeeId, String name, String contactNumber, String designation, String school_code,
                               String schoolName, String district) {
        this.employeeId = employeeId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.designation = designation;
        this.schoolCode = school_code;
        this.schoolName = schoolName;
        this.isPresent = false;
        this.district = district;
        this.temp = 0.0f;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
