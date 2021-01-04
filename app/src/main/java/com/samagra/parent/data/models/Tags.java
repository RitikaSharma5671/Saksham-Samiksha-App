package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Umang Bhola on 14/5/20.
 * Samagra- Transforming Governance
 */
public class Tags implements Serializable{

    @SerializedName("FORMID")
    @Expose
    private String formId;
    @SerializedName("FORMNAME")
    @Expose
    private String formName;
    @SerializedName("USERNAME")
    @Expose
    private String username;
    @SerializedName("INSTANCEID")
    @Expose
    private String instanceId;
    @SerializedName("FORMSUBMISSIONDATE")
    @Expose
    private String formSubmissionDate;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getUsername() {
        return username;
    }

    public void setUSERNAME(String username) {
        this.username = username;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Date getFormSubmissionDate() {

        if (formSubmissionDate == null || formSubmissionDate.isEmpty())
            return new Date();
        else {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            // DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            try {
                return df1.parse(formSubmissionDate);
            } catch (ParseException | NullPointerException e) {
                e.printStackTrace();
                return new Date();
            }
        }
    }

    public String getUnprocessedDate(){
        return formSubmissionDate;
    }

    public void setFormSubmissionDate(String formSubmissionDate) {
        this.formSubmissionDate = formSubmissionDate;
    }

}