package com.example.student_details.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DBSchoolInfo extends RealmObject {

    @PrimaryKey
    public String schoolCode;
    public String District;
    public String Block;
    public String SchoolName;

    public DBSchoolInfo() {
    }

    public DBSchoolInfo(String schoolCode, String district, String block, String schoolName) {
        this.schoolCode = schoolCode;
        District = district;
        Block = block;
        SchoolName = schoolName;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }
}
