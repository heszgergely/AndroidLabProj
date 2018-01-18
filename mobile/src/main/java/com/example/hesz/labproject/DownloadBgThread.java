package com.example.hesz.labproject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gergelyhesz on 18/01/2018.
 */

public class DownloadBgThread  extends Thread {

    private static final String TAG = "DownloadBgThread";


    Handler handler;
    boolean isRun = true;

    private static final String JOBS_URL = Constants.REST_SERVER_ADDRESS + Constants.JOBS_ADDRESS_SUFFIX;


    private DataBaseHelper db;


    public DownloadBgThread(Handler handler, Context context){
        db = new DataBaseHelper(context);
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        while(isRun){

            downloadJobs();

            //sendMessage("test message");

            try{
                Thread.sleep(10000);
            }catch (Exception e) {}
        }
    }



    private void downloadJobs() {

        URL url = null;
        String downloadedString;
        try {
            url = new URL(JOBS_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            downloadedString = downloadUrl(url);
            JSONArray jsonArray = new JSONArray(downloadedString);
            List<JobItem> jobList = JobItem.parseJobsFromJSON(jsonArray);
            compareToDatabase(jobList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    private void compareToDatabase(List<JobItem> downloadedList) {
        LinkedList<JobItem> jobsFromDataBase;

        jobsFromDataBase = db.getALlJobs();
        for(JobItem downloadedJob: downloadedList){
            boolean found = false;
            for(JobItem dbJob: jobsFromDataBase){
                if(downloadedJob.jobId==dbJob.jobId){
                    found = true;
                    if(!downloadedJob.status.equals(dbJob.status)) {
                        sendMessage("Job " + downloadedJob.jobId +
                                " changed from "+dbJob.status+" to "+downloadedJob.status);
                    }
                    db.modifyJob(downloadedJob);
                }
            }
            if(!found){
                db.createJob(downloadedJob);
                sendMessage("New Job " + downloadedJob.jobId +
                        " with "+downloadedJob.status+" status");
            }
        }
    }


    private void sendMessage(String message){
        Log.d(TAG,"sendMessage called");
        Message msg = Message.obtain();
        msg.obj = message;
        handler.sendMessage(msg);
    }

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Username:","user");
            connection.addRequestProperty("Password:","password");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 3000);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }



    /**
     * Converts the contents of an InputStream to a String.
     */
    public String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

}
