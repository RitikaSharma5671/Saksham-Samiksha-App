package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Umang Bhola on 14/5/20.
 * Samagra- Transforming Governance
 */
public class PDFAPIResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private PDFData pdfData;

    public PDFData getPDFData() {
        return pdfData;
    }

    public void setPDFData(PDFData data) {
        this.pdfData = data;
    }

}
