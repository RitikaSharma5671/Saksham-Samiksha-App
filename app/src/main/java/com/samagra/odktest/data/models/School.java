package com.samagra.odktest.data.models;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Double.parseDouble;

/**
 * A POJO for representing the a School.
 */
public class School {

    public String district;
    public String block;
    public String cluster;
    public String village;
    public String udise;
    public String schoolName;
    public String gp;
    String schoolType;
    String ruralOrUrban;
    String academicCycle;
    String pinCode;
    String latDeg;
    String latMin;
    String latSec;
    String lonDeg;
    String lonMin;
    String lonSec;

    public String getStringForSearch() {
        return this.district + " "
                + this.block + " "
                + this.cluster + " "
                + this.village + " "
                + this.udise + " "
                + this.schoolName;
    }

    public School(String district, String block, String cluster, String udise, String schoolName) {
        this.schoolName = schoolName;
        this.cluster = cluster;
        this.block = block;
        this.udise = udise;
        this.district = district;
    }

    public School(String district, String block, String cluster, String udise, String schoolName, String gp) {
        this.schoolName = schoolName;
        this.cluster = cluster;
        this.block = block;
        this.udise = udise;
        this.district = district;
        this.gp = gp;
    }

    LatLng getLatLon() {
        return new LatLng(getLatDegree(), getLonDegree());
    }

    Double getLatDegree() {
        return parseDouble(this.latDeg) + parseDouble(this.latMin) / 60.0 + parseDouble(this.latSec) / 3600.0;
    }

    Double getLonDegree() {
        return parseDouble(this.lonDeg) + parseDouble(this.lonMin) / 60.0 + parseDouble(this.lonSec) / 3600.0;
    }
}
