package com.samagra.commons;

/**
 * A POJO for representing the a InstitutionInfo.
 */
public class InstitutionInfo {

    private String District;
    private String Block;
    private String SchoolName;
    private int schoolCode;

    public String getStringForSearch() {
        return this.District + " "
                + this.Block + " "
                + this.SchoolName;
    }

    public InstitutionInfo(String district, String block, String schoolName) {
        this.Block = block;
        this.SchoolName = schoolName;
        this.District = district;
    }

    public InstitutionInfo(String district, String block, String schoolName, int schoolCode) {
        this.Block = block;
        this.SchoolName = schoolName;
        this.District = district;
        this.schoolCode = schoolCode;
    }

    public String getDistrict() {
        return District;
    }

    public String getBlock() {
        return Block;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public int getSchoolCode() {
        return schoolCode;
    }
}
