package com.example.student_details.models.realm;

import android.util.Log;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class StudentInfo extends RealmObject {

    private boolean isSMRegistered;
    @Required
    @PrimaryKey
    private String srn;

    @Required
    private String name;

    private int grade;

    private String section;

    private String stream;

    private String fatherName;


    private String fatherContactNumber;

    private boolean isPresent;

    public String getShikshaMitrName() {
        return shikshaMitrName;
    }

    public void setShikshaMitrName(String shikshaMitrName) {
        this.shikshaMitrName = shikshaMitrName;
    }

    public String getShikshaMitrContact() {
        return shikshaMitrContact;
    }

    public void setShikshaMitrContact(String shikshaMitrContact) {
        this.shikshaMitrContact = shikshaMitrContact;
    }

    public String getShikshaMitrRelation() {
        return shikshaMitrRelation;
    }

    public void setShikshaMitrRelation(String shikshaMitrRelation) {
        this.shikshaMitrRelation = shikshaMitrRelation;
    }

    public String getShikshaMitrAddress() {
        return shikshaMitrAddress;
    }

    public String getSchool_code() {
        return school_code;
    }

    public void setSchool_code(String school_code) {
        this.school_code = school_code;
    }

    private String shikshaMitrName;
    private String shikshaMitrContact;
    private String shikshaMitrRelation;

    public void setShikshaMitrAddress(String shikshaMitrAddress) {
        this.shikshaMitrAddress = shikshaMitrAddress;
    }

    private String shikshaMitrAddress;

    private float temp;
    @Required
    private String school_code;

    public StudentInfo() {
    }
    public StudentInfo(String srn, String name, int grade, String section, String stream,
                       String fatherName, String fatherContactNumber, String school_code
            , String shikshaMitrName, String shikshaMitrContact, String shikshaMitrRelation, String shikshaMitrAddress,
                       boolean isSMRegistered) {
        this.srn = srn;
        this.name = name;
        this.grade = grade;
        this.section = section;
        this.stream = stream;
        this.fatherName = fatherName;
        this.fatherContactNumber = fatherContactNumber;
        this.school_code = school_code;
        this.isPresent = false;
        this.isSMRegistered = isSMRegistered;
        this.temp = 0.0f;
        if(shikshaMitrRelation != null && !shikshaMitrRelation.equals(""))
            this.shikshaMitrRelation =shikshaMitrRelation;
         else   this.shikshaMitrRelation ="-";
        if(shikshaMitrName != null && !shikshaMitrName.equals(""))
            this.shikshaMitrName = shikshaMitrName;
        else this.shikshaMitrName ="-";
        if(shikshaMitrContact != null && !shikshaMitrContact.equals(""))
            this.shikshaMitrContact =shikshaMitrContact ;
        else  this.shikshaMitrContact ="-";
        Log.d("gsgsgs", "SRN " + srn + " address " + shikshaMitrAddress);
        if(shikshaMitrAddress != null && !shikshaMitrAddress.equals(""))
             this.shikshaMitrAddress =shikshaMitrAddress ;
         else  this.shikshaMitrAddress ="-";
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

    public boolean isSMRegistered() {
        return isSMRegistered;
    }

    public void setSMRegistered(boolean SMRegistered) {
        isSMRegistered = SMRegistered;
    }
}
