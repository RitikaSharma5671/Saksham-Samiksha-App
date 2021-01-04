package com.samagra.parent.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
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

    public FetchUserSubmittedPDFModel(GetPDFListener listener, String userName) {
        this.listener = listener;
        this.userName = userName;
    }

    @SuppressWarnings({"ConstantConditions"})
    @Override
    protected ArrayList<PDFItem> doInBackground(String[] strings) {

        String userName = this.userName;
        String url = "http://68.183.94.187:6001/v1/graphql";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json,text/plain");
        RequestBody body = RequestBody.create("{\"query\":\"query MyQuery {\n" +
                "  outputtable(where: {tags: {_contains: {USERNAME: "+ userName+ "}}}) {\n" +
                "    doc_name\n" +
                "    tags\n" +
                "  }\n" +
                "}\"\n" +
                "}", mediaType);
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
                Log.d("RESPSS", "RESPONSEEEEEEEEEEE   " + gg);
                PDFAPIResponse example = new Gson().fromJson(gg, PDFAPIResponse.class);
                if(example.getPDFData() != null && example.getPDFData().getPDFItems() != null){
                    return example.getPDFData().getPDFItems();
                }else{
                    return null;
                }
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
