package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Umang Bhola on 14/5/20.
 * Samagra- Transforming Governance
 */

public class Tags_ implements Serializable {

    @SerializedName("FORMID")
    @Expose
    private String fORMID;
    @SerializedName("FORMNAME")
    @Expose
    private String fORMNAME;
    @SerializedName("USERNAME")
    @Expose
    private String uSERNAME;
    @SerializedName("INSTANCEID")
    @Expose
    private String iNSTANCEID;
    @SerializedName("FORMSUBMISSIONDATE")
    @Expose
    private String fORMSUBMISSIONDATE;

    public String getFORMID() {
        return fORMID;
    }

    public void setFORMID(String fORMID) {
        this.fORMID = fORMID;
    }

    public String getFORMNAME() {
        return fORMNAME;
    }

    public void setFORMNAME(String fORMNAME) {
        this.fORMNAME = fORMNAME;
    }

    public String getUSERNAME() {
        return uSERNAME;
    }

    public void setUSERNAME(String uSERNAME) {
        this.uSERNAME = uSERNAME;
    }

    public String getINSTANCEID() {
        return iNSTANCEID;
    }

    public void setINSTANCEID(String iNSTANCEID) {
        this.iNSTANCEID = iNSTANCEID;
    }

    public String getFORMSUBMISSIONDATE() {
        return fORMSUBMISSIONDATE;
    }

    public void setFORMSUBMISSIONDATE(String fORMSUBMISSIONDATE) {
        this.fORMSUBMISSIONDATE = fORMSUBMISSIONDATE;
    }

}
