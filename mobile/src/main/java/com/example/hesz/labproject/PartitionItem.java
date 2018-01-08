package com.example.hesz.labproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hesz on 1/8/2018.
 */

public class PartitionItem {



    public String partitionName;
    private static final String tagPartitionName = "partition";

    public String availability;
    private static final String tagAvailability = "avail";

    public String timelimit;
    private static final String tagTimelimit = "timelimit";

    public String nodeStates;
    private static final String tagNodeStates = "nodes";

    public String nodelist;
    private static final String tagNodelist = "nodelist";


    /*
 {
      "partition":"batch",
      "avail":"up",
      "timelimit":"infinite",
      "nodes":"2\/6\/0\/8",
      "nodelist":"adev[8-15]"
   },
    */

    public PartitionItem (String partitionName, String availability, String timelimit,
                          String nodeStates, String nodelist) {
        this.partitionName = partitionName;
        this.availability = availability;
        this.timelimit = timelimit;
        this.nodeStates = nodeStates;
        this.nodelist = nodelist;
    }


    public static ArrayList<PartitionItem> parsePartitionsFromJSON(JSONArray partitions) throws JSONException {
        ArrayList<PartitionItem> parsedList = new ArrayList<PartitionItem>();

        for(int i = 0; i<partitions.length(); i++){
            JSONObject obj = partitions.getJSONObject(i);
            PartitionItem parsedPartition = new PartitionItem(
                    obj.getString(tagPartitionName),
                    obj.getString(tagAvailability),
                    obj.getString(tagTimelimit),
                    obj.getString(tagNodeStates),
                    obj.getString(tagNodelist));
            parsedList.add(parsedPartition);
        }


        return parsedList;

    }

}
