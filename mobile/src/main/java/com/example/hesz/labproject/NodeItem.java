package com.example.hesz.labproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gergelyhesz on 07/01/2018.
 */

public class NodeItem {

    public String nodeName;
    private static final String tagNodeName = "nodelist";

    public int nodeNumber;
    private static final String tagNodeNumber = "nodes";

    public String partitionName;
    private static final String tagPartitionName = "partition";

    public String state;
    private static final String tagState = "state";

    public int cpuNumber;
    private static final String tagCpuNumber = "cpus";

    public int memory;
    private static final String tagMemory = "memory";

    public int tmpDisk;
    private static final String tagTmpDisk = "tmpdisk";

    public int weight;
    private static final String tagWeight = "weight";

    /*
    "nodelist":"adev[0-1]",
    "nodes":"2",
    "partition":"debug",
    "state":"idle",
    "cpus":"2",
    "memory":"3448",
    "tmpdisk":"38536",
    "weight":"16"
    */

    public NodeItem (String nodeName, int nodeNumber, String partitionName, String state, int cpuNumber,
                     int memory, int tmpDisk, int weight) {
        this.nodeName = nodeName;
        this.nodeNumber = nodeNumber;
        this.partitionName = partitionName;
        this.state = state;
        this.cpuNumber = cpuNumber;
        this.memory = memory;
        this.tmpDisk = tmpDisk;
        this.weight = weight;
    }


    public static ArrayList<NodeItem> parseNodesFromJSON(JSONArray nodes) throws JSONException {
        ArrayList<NodeItem> parsedList = new ArrayList<NodeItem>();

        for(int i = 0; i<nodes.length(); i++){
            JSONObject obj = nodes.getJSONObject(i);
            NodeItem parsedNode = new NodeItem(
                obj.getString(tagNodeName),
                obj.getInt(tagNodeNumber),
                obj.getString(tagPartitionName),
                obj.getString(tagState),
                obj.getInt(tagCpuNumber),
                obj.getInt(tagMemory),
                obj.getInt(tagTmpDisk),
                obj.getInt(tagWeight));
            parsedList.add(parsedNode);
        }


        return parsedList;

    }



}
