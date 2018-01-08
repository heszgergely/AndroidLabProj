package com.example.hesz.labproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hesz on 1/8/2018.
 */

public class JobItem {



    public int jobId;
    private static final String tagJobId = "jobid";

    public String partitionName;
    private static final String tagPartitionName = "partition";

    public String jobName;
    private static final String tagJobName = "name";

    public String submittingUser;
    private static final String tagSubmittingUser = "user";

    public String status;
    private static final String tagStatus = "status";

    public String time;
    private static final String tagTime = "time";

    public int numberOfNodes;
    private static final String tagNumberOfNodes = "nodes";

    public String nodelist;
    private static final String tagNodelist = "nodelist";



    /*
{
      "jobid":"3456",
      "partition":"debug",
      "name":"Job 1 name",
      "user":"user 1",
      "status":"running",
      "time":"1-20:51:21",
      "nodes":"1",
      "nodelist":"adev0"
   },
    */

    public JobItem (int jobId, String partitionName, String jobName,
                    String submittingUser, String status, String time,
                    int numberOfNodes, String nodelist) {

        this.jobId = jobId;
        this.partitionName = partitionName;
        this.jobName = jobName;
        this.submittingUser = submittingUser;
        this.status = status;
        this.time = time;
        this.numberOfNodes = numberOfNodes;
        this.nodelist = nodelist;
    }


    public static ArrayList<JobItem> parseJobsFromJSON(JSONArray partitions) throws JSONException {
        ArrayList<JobItem> parsedList = new ArrayList<JobItem>();

        for(int i = 0; i<partitions.length(); i++){
            JSONObject obj = partitions.getJSONObject(i);
            JobItem parsedJob = new JobItem(
                    obj.getInt(tagJobId),
                    obj.getString(tagPartitionName),
                    obj.getString(tagJobName),
                    obj.getString(tagSubmittingUser),
                    obj.getString(tagStatus),
                    obj.getString(tagTime),
                    obj.getInt(tagNumberOfNodes),
                    obj.getString(tagNodelist));
            parsedList.add(parsedJob);
        }


        return parsedList;

    }



}
