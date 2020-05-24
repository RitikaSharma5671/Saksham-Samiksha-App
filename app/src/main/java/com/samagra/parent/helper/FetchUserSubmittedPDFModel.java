package com.samagra.parent.helper;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.samagra.ancillaryscreens.screens.passReset.SendOTPTask;
import com.samagra.parent.data.models.PDFAPIResponse;
import com.samagra.parent.data.models.GetPDFListener;
import com.samagra.parent.data.models.PDFItem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FetchUserSubmittedPDFModel extends AsyncTask<String, Void, ArrayList<PDFItem>> {

    private final String userName;
    private GetPDFListener listener;
    private String TAG = SendOTPTask.class.getName();

    public FetchUserSubmittedPDFModel(GetPDFListener listener, String userName) {
        this.listener = listener;
        this.userName = userName;
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    protected ArrayList<PDFItem> doInBackground(String[] strings) {

        String userName = this.userName;
        String url = "http://159.89.163.33:5001/v1/graphql";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json,text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\"query\":\"query MyQuery {\n  queuemanager(where: {tags: {_contains: {\n    USERNAME:\\\"" + userName + "\\\"\n  }}}) {\n    doc_name\n    instance_id\n    current_status\n    tags\n    Pdfgenerated {\n      doc_name\n      instance_id\n      tags\n    }\n  }\n}\"\n}\n");
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-hasura-admin-secret", "2OWslm5aAjlTARU")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.code() == HttpURLConnection.HTTP_OK) {
                String gg = response.body().string();
                response.body().contentType();
                PDFAPIResponse example = new Gson().fromJson(gg, PDFAPIResponse.class);
                if(example.getPDFData() != null && example.getPDFData().getPDFItems() != null){
                    return example.getPDFData().getPDFItems();
                }else{
                    return null;
                }

//                LinkedTreeMap linkedTreeMap = gson.fromJson(gg, LinkedTreeMap.class);
//                ArrayList<LinkedTreeMap> queuemanagers = new Gson().fromJson(new Gson().toJsonTree(linkedTreeMap).getAsJsonObject().get("data").getAsJsonObject().get("queuemanager").toString(), ArrayList.class);
//
//                ArrayList<Queuemanager> queuemanager = new ArrayList<>();
//                for (LinkedTreeMap q : queuemanagers) {
//                    Queuemanager queuemanager1 = new Queuemanager();
////                    if(q.get("doc_name") != null)
////                        queuemanager1.setDocName(q.get("doc_name").toString());
//                    queuemanager1.setDocName("https://storage.googleapis.com/pdf-builder-samagra/9c2184f5-13a3-4855-b5fc-7b47c974a95f.pdf?Expires=4112337441&GoogleAccessId=kamal-447%40testapp-269913.iam.gserviceaccount.com&Signature=HDKkcSspRv4VLOXUIjTYG1i%2BDl6NPbC7k4KMYV%2Fju3x%2BPLz3HeitOq3QM2gJ4X5%2FrDUYbdwToWsRMM3RD8tnoVdRU1oC3FXxz3D2FHIIIF4lkxsKNdIz77iGnqbifs%2BCu1cTZbDnHy3%2BL0CcEAoQui4%2FhmEpjwVzAkZW8oI1oT7qS%2F77QqrgS3dtlTQtskbAw%2Fhg0gWCPKjIdQJpF6MQJoTXSySe8eE9YVZpQgAJIGcL9BL8u4qRHMT6ECQVSwTgwW9nlWAExKgE5byOjjPWvCy5nPM0RxYz5ABabHViZp0e4iJ%2FfgiOOyNSufa49Sm8sTdD7%2BFwO5V2k%2FavYkvBXw%3D%3D");
////                    else
////                        queuemanager1.setDocName(null);
//
//                    if (q.get("current_status") != null)
//                        queuemanager1.setCurrentStatus(q.get("current_status").toString());
//                    else
//                        queuemanager1.setCurrentStatus(null);
//                    if (q.get("instance_id") != null)
//                        queuemanager1.setInstanceId(q.get("instance_id").toString());
//                    else
//                        queuemanager1.setInstanceId(null);
//                    Tags tags = new Tags();
//                    if (q.get("tags") != null) {
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMNAME") != null) {
//                            tags.setFORMNAME(((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMNAME").toString());
//                        }
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMID") != null) {
//                            tags.setFORMID(((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMID").toString());
//                        }
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("INSTANCEID") != null) {
//                            tags.setINSTANCEID(((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("INSTANCEID").toString());
//                        }
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMSUBMISSIONDATE") != null) {
//                            tags.setFORMSUBMISSIONDATE(((LinkedTreeMap) queuemanagers.get(0).get("tags")).get("FORMSUBMISSIONDATE").toString());
//                        }
//                        queuemanager1.setTags(tags);
//                    } else {
//                        queuemanager1.setTags(tags);
//                    }
//
//                    Pdfgenerated pdfgenerated = new Pdfgenerated();
//                    if (q.get("Pdfgenerated") != null) {
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("Pdfgenerated")).get("doc_name") != null) {
//                            pdfgenerated.setDocName(((LinkedTreeMap) queuemanagers.get(0).get("Pdfgenerated")).get("doc_name").toString());
//                        }
//                        if (((LinkedTreeMap) queuemanagers.get(0).get("Pdfgenerated")).get("instance_id") != null) {
//                            pdfgenerated.setDocName(((LinkedTreeMap) queuemanagers.get(0).get("Pdfgenerated")).get("instance_id").toString());
//                        }
//                        queuemanager1.setPdfgenerated(pdfgenerated);
//                    } else {
//                        queuemanager1.setPdfgenerated(pdfgenerated);
//                    }
//                    queuemanager.add(queuemanager1);
//                }
//                return queuemanager;
            } else {
                if (!response.isSuccessful())
                    listener.onFailure(new Exception("Network Request could not be completed"), FailureType.HTTPS_RESPONSE_ERROR);
                else
                    listener.onFailure(new Exception("Failed with status code " + response.code()), FailureType.NETWORK_REQUEST_NOT_SUCCESSFUL);

                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(ArrayList<PDFItem> s) {
        if (s == null)
            listener.onFailure(new Exception("Failed"), FailureType.NULL_ARRAY_LIST);
        else
            listener.onSuccess(s);
    }

    public enum FailureType {
        HTTPS_RESPONSE_ERROR,
        NETWORK_REQUEST_NOT_SUCCESSFUL,
        NULL_ARRAY_LIST
    }
}
