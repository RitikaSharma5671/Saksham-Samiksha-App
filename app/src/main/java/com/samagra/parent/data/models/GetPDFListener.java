package com.samagra.parent.data.models;

import com.samagra.parent.helper.FetchUserSubmittedPDFModel;

import java.util.ArrayList;

public interface GetPDFListener {
    void onSuccess(ArrayList<PDFItem> pdfItemArrayList);

    /**
     * The operation failed
     *
     * @param exception which caused the failure
     */
    void onFailure(Exception exception, FetchUserSubmittedPDFModel.FailureType failureType);
}
