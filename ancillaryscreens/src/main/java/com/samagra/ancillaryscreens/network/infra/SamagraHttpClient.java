package com.samagra.ancillaryscreens.network.infra;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.grove.logging.Grove;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class SamagraHttpClient {//private static GBLogger LOG = new GBLogger(GBHttpClient.class);
    //private static GBLogger LOG = new GBLogger(GBHttpClient.class);
    private int READ_TIMEOUT;   // constant for Http read timout (in milliseconds)
    private int CONNECTION_TIMEOUT; // constant for connection time out (in milliseconds)
    private static int mStatusCode = 1;


    public enum GBRequestType {
        GET,
        PUT,
        POST,
        DELETE
    }

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_TEXT = "text";
    public static final String CONTENT_TYPE_APPLICATION = "application/x-www-form-urlencoded; charset=UTF-8";


    /**
     * Constructor to initialize client.
     *
     * @param READ_TIMEOUT
     * @param CONNECTION_TIMEOUT
     */
    public SamagraHttpClient(int READ_TIMEOUT, int CONNECTION_TIMEOUT) {
        this.READ_TIMEOUT = READ_TIMEOUT;
        this.CONNECTION_TIMEOUT = CONNECTION_TIMEOUT;
        System.getProperty("http.keepAlive","true");
        Grove.d("Keep Alive : " + System.getProperty("http.keepAlive"));
    }

    /**
     * use this method to post data over http/https connection in current thread (blocking)
     *
     * @param request
     * @return - returns a http response data in string format
     * @throws IOException
     */
    public String fetchResponse(SamagraNetworkRequest request) throws IOException {
        String url = request.getEncodedUrlWithQueryParameters();
        InputStream is = null;
        long startTime = System.currentTimeMillis();
        HttpURLConnection conn = null;
        try {
            URL _url = new URL(url);

            conn = (HttpURLConnection) _url.openConnection();
            conn.setReadTimeout(request.getRequestTimeout() /* milliseconds */);
            conn.setConnectTimeout(CONNECTION_TIMEOUT /* milliseconds */);
            conn.setRequestMethod(request.getType().toString());
            conn.setRequestProperty("Accept",request.getContentType());
            conn.setRequestProperty("Content-type",request.getContentType());
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", AncillaryScreensDriver.API_KEY);



            if ((request.getType() == GBRequestType.DELETE || request.getType() == GBRequestType.POST || request.getType() == GBRequestType.PUT) && request.getPostData() != null) {
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                Grove.d("Connecting to url :" + url);
                // Starts the query
                long conStartTime = System.currentTimeMillis();
                conn.connect();
                Grove.d(String.format("RequestNo: %d  GBHttpClient>>Time taken for conn.connect() is %d, for URL %s %s: ",request.requestIndex,(int) (System.currentTimeMillis() - conStartTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));

                Grove.d("Successfully Connected to url :" + url);
                // write the post data to the output stream i.e., the form data part
                OutputStream os = conn.getOutputStream();
                Grove.d("GBHttpClient >> fetch response for POST data  " + request.getPostData());
                conStartTime = System.currentTimeMillis();
                BufferedWriter writer;
                writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));  //TODO: check it's performance
                writer.write(request.getPostData());

                writer.flush();
                writer.close();
                os.close();
                Grove.d(String.format("RequestNo: %d  GBHttpClient>>Time taken for POST is %d, for URL %s %s: ",request.requestIndex,(int) (System.currentTimeMillis() - conStartTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));
            } else {
                long conStartTime = System.currentTimeMillis();
                conn.connect();
                Grove.d(String.format("RequestNo: %d  GBHttpClient>>Time taken for conn.connect() is %d, for URL %s %s: ",request.requestIndex,(int) (System.currentTimeMillis() - conStartTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));
            }
            // connect to the service and get the output.
//            conn.connect();


            // Read the input stream we get after we make the connection and see
            // the result.
            String result = null;
            long isStartTime = System.currentTimeMillis();
            is = conn.getInputStream();
            result = generateOp(is,"gzip".equals(conn.getContentEncoding())); // Response fron Api
            Grove.d(String.format("RequestNo: %d  GBHttpClient>>Time taken for GET  is %d, for URL %s %s: ",request.requestIndex,(int) (System.currentTimeMillis() - isStartTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));

            Grove.d(String.format("RequestNo: %d  GBHttpClient>>Total Time taken for GET and POST is %d, for URL %s %s: ",request.requestIndex,(int) (System.currentTimeMillis() - startTime),request.getType().name(),request.getEncodedUrlWithQueryParameters()));
            Grove.d("response result :" + result);
            return result;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (SocketTimeoutException ex) {
            Grove.e("SocketTimeoutExceptio for url : " + request.getEncodedUrlWithQueryParameters(),ex);
            Grove.d("Re-trying....");
            if (request.getNumberOfRetryAttempts() > 0) {
                request.setNumberOfRetryAttempts(request.getNumberOfRetryAttempts() - 1);
                return fetchResponse(request);
            } else {
                throw ex;
            }
        } finally {
            if (is != null) {
                Grove.d("Closing Connection....");
                is.close();
            }
            if (conn != null) {
                handleHttpResponseStatus(conn.getResponseCode());
                conn.disconnect();
            }
        }
    }

    private void handleHttpResponseStatus(int responseCode) {
       
    }

    private static String generateOp(InputStream inputStream,boolean isGzipEnabled) throws IOException {
        String line;
        // ANDMAP-1269
        StringBuilder result = new StringBuilder("");
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
