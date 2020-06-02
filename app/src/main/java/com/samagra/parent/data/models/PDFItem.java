package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class PDFItem implements Serializable {

    @SerializedName("doc_name")
    @Nullable
    private String docName;

    @SerializedName("instance_id")
    @Expose
    private String instanceId;
    @SerializedName("current_status")
    @Expose
    private String currentStatus;
    @SerializedName("tags")
    @Expose
    private Tags pdfItemTags;

    @SerializedName("outputtables")
    @Expose
    private ArrayList<Pdfgenerated> outputData;

    public String getPDFDocLink() {
        return docName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Tags getPDFItemTags() {
        return pdfItemTags;
    }

    public void setTags(Tags tags) {
        this.pdfItemTags = tags;
    }

    public ArrayList<Pdfgenerated> getOutputData() {
        return outputData;
    }

    public void setOutputData(ArrayList<Pdfgenerated> pdfgenerated) {
        this.outputData = pdfgenerated;
    }

}
