package com.samagra.parent.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pdfgenerated implements Serializable {

   @SerializedName("doc_name")
   @Expose
   private String  docName;
   @SerializedName("instance_id")
   @Expose
   private String instanceId;
   @SerializedName("tags")
   @Expose
   private Tags_ tags;

   public String getDocName() {
       return docName;
   }

   public void setDocName(String docName) {
       this.docName = docName;
   }

   public String getInstanceId() {
       return instanceId;
   }

   public void setInstanceId(String instanceId) {
       this.instanceId = instanceId;
   }

   public Tags_ getTags() {
       return tags;
   }

   public void setTags(Tags_ tags) {
       this.tags = tags;
   }

}
