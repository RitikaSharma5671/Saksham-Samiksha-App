package com.samagra.commons;

/**
 * A POJO for representing the a InstitutionInfo.
 */
public class InstitutionInfo {

    public String District;
    public String Block;
    public String SchoolName;

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

}
