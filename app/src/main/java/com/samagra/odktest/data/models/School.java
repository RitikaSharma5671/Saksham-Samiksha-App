package com.samagra.odktest.data.models;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Double.parseDouble;

/**
 * A POJO for representing the a School.
 */
public class School {

    public String district;
    public String block;
    public String schoolName;
    public String schoolCode;
    public String schoolType;
    public String isPrimary;
    public String isElementary;
    public String elemMentor;
    public String elemMonitor;
    public String SAT;
    public String secMentor;
    public String secMonitor;
    public String elemSsa;
    public String eecSsa;
    public String sampark;

    public School(String district, String block, String schoolName, String schoolCode, String schoolType, String isPrimary, String isElementary, String elemMentor, String elemMonitor, String SAT, String secMentor, String secMonitor, String elemSsa, String eecSsa, String sampark) {
        this.district = district;
        this.block = block;
        this.schoolName = schoolName;
        this.schoolCode = schoolCode;
        this.schoolType = schoolType;
        this.isPrimary = isPrimary;
        this.isElementary = isElementary;
        this.elemMentor = elemMentor;
        this.elemMonitor = elemMonitor;
        this.SAT = SAT;
        this.secMentor = secMentor;
        this.secMonitor = secMonitor;
        this.elemSsa = elemSsa;
        this.eecSsa = eecSsa;
        this.sampark = sampark;
    }

    public School(String district, String block, String schoolName) {
        this.district = district;
        this.block = block;
        this.schoolName = schoolName;
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getIsElementary() {
        return isElementary;
    }

    public void setIsElementary(String isElementary) {
        this.isElementary = isElementary;
    }

    public String getElemMentor() {
        return elemMentor;
    }

    public void setElemMentor(String elemMentor) {
        this.elemMentor = elemMentor;
    }

    public String getElemMonitor() {
        return elemMonitor;
    }

    public void setElemMonitor(String elemMonitor) {
        this.elemMonitor = elemMonitor;
    }

    public String getSAT() {
        return SAT;
    }

    public void setSAT(String SAT) {
        this.SAT = SAT;
    }

    public String getSecMentor() {
        return secMentor;
    }

    public void setSecMentor(String secMentor) {
        this.secMentor = secMentor;
    }

    public String getSecMonitor() {
        return secMonitor;
    }

    public void setSecMonitor(String secMonitor) {
        this.secMonitor = secMonitor;
    }

    public String getElemSsa() {
        return elemSsa;
    }

    public void setElemSsa(String elemSsa) {
        this.elemSsa = elemSsa;
    }

    public String getEecSsa() {
        return eecSsa;
    }

    public void setEecSsa(String eecSsa) {
        this.eecSsa = eecSsa;
    }

    public String getSampark() {
        return sampark;
    }

    public void setSampark(String sampark) {
        this.sampark = sampark;
    }
}
