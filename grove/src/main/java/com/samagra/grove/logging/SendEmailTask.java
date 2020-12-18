package com.samagra.grove.logging;

import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

public class SendEmailTask extends AsyncTask<String, Void, SendEmailResult> {

    private String to;
    private Destination destination;
    private Message message;
    private String TAG = SendEmailTask.class.getName();
    private boolean isSuccess;
    private String serverURL;
    private String from;
    private CognitoCachingCredentialsProvider credentials;
    private EmailListener listener;

    public SendEmailTask(String from, String to, Destination destination, Message message, CognitoCachingCredentialsProvider credentials, EmailListener listener) {
        this.to = to;
        this.from = from;
        this.destination = destination;
        this.message = message;
        this.listener = listener;
        this.credentials = credentials;
    }

    @Override
    protected SendEmailResult doInBackground(String[] strings) {
        final AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(credentials);
        ses.setRegion(Region.getRegion(Regions.US_WEST_2));

        SendEmailRequest request = new SendEmailRequest(from, destination, message);
        Grove.e("Triggering Send email Task >>>> ");
        SendEmailResult sendEmailResult = ses.sendEmail(request);
        Grove.e("Triggered Send email Task returned result >>>> " + sendEmailResult);
        return sendEmailResult;
    }

    protected void onPostExecute(SendEmailResult s) {
        Grove.e("Crash report sending request has been triggered");
        if (s != null && s.getMessageId() != null) {
            Grove.e("Crash report successfully sent to the back-end Server.");
        } else {
            Grove.e("Crash report could not be sent to the back-end Server.");
        }
        listener.onDone(s);
    }
}
