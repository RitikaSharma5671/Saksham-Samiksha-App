package com.samagra.parent.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Umang Bhola on 14/5/20.
 * Samagra- Transforming Governance
 */
public class POJOClass implements Serializable {

    @SerializedName("data")
    private PDFData data;

    public PDFData getData() {
        return data;
    }

    public void setData(PDFData data) {
        this.data = data;
    }

}


