package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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
    @SerializedName("Pdfgenerated")
    @Expose
    private Pdfgenerated pdfgenerated;

    public String getPDFDocLink() {
        return docName;
//        return "https://storage.googleapis.com/pdf-builder-samagra/9c2184f5-13a3-4855-b5fc-7b47c974a95f.pdf?Expires=4112337441&GoogleAccessId=kamal-447%40testapp-269913.iam.gserviceaccount.com&Signature=HDKkcSspRv4VLOXUIjTYG1i%2BDl6NPbC7k4KMYV%2Fju3x%2BPLz3HeitOq3QM2gJ4X5%2FrDUYbdwToWsRMM3RD8tnoVdRU1oC3FXxz3D2FHIIIF4lkxsKNdIz77iGnqbifs%2BCu1cTZbDnHy3%2BL0CcEAoQui4%2FhmEpjwVzAkZW8oI1oT7qS%2F77QqrgS3dtlTQtskbAw%2Fhg0gWCPKjIdQJpF6MQJoTXSySe8eE9YVZpQgAJIGcL9BL8u4qRHMT6ECQVSwTgwW9nlWAExKgE5byOjjPWvCy5nPM0RxYz5ABabHViZp0e4iJ%2FfgiOOyNSufa49Sm8sTdD7%2BFwO5V2k%2FavYkvBXw%3D%3D";
    }

    public void setDocName(@Nullable String docName) {
        this.docName = docName;
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

    public Pdfgenerated getPdfgenerated() {
        return pdfgenerated;
    }

    public void setPdfgenerated(Pdfgenerated pdfgenerated) {
        this.pdfgenerated = pdfgenerated;
    }

}
