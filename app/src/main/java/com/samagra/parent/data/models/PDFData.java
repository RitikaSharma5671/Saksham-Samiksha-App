package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFData implements Serializable {

    @SerializedName("queuemanager")
    @Expose
    private ArrayList<PDFItem> pdfItems = null;

    public ArrayList<PDFItem> getPDFItems() {
        return pdfItems;
    }

    public void setPDFItems(ArrayList<PDFItem> pdfItems) {
        this.pdfItems = pdfItems;
    }
}
