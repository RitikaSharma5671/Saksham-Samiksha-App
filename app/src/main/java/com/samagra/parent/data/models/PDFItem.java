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

    @SerializedName("tags")
    @Expose
    private Tags pdfItemTags;

    public String getPDFDocLink() {
        return docName;
    }

    public Tags getPDFItemTags() {
        return pdfItemTags;
    }




}
