package com.example.hesz.labproject;

/**
 * Created by gergelyhesz on 08/01/2018.
 */

public class Content {
    public static final String nodes = "[{\"nodelist\":\"adev[0-1]\",\"nodes\":\"2\",\"partition\":\"debug\",\"state\":\"idle\",\"cpus\":\"2\",\"memory\":\"3448\",\"tmpdisk\":\"38536\",\"weight\":\"16\"},{\"nodelist\":\"adev[2,4-7]\",\"nodes\":\"5\",\"partition\":\"debug\",\"state\":\"idle\",\"cpus\":\"2\",\"memory\":\"3448\",\"tmpdisk\":\"38536\",\"weight\":\"16\"},{\"nodelist\":\"adev3\",\"nodes\":\"1\",\"partition\":\"debug\",\"state\":\"allocated\",\"cpus\":\"2\",\"memory\":\"3384\",\"tmpdisk\":\"38536\",\"weight\":\"16\"},{\"nodelist\":\"adev[8-9]\",\"nodes\":\"2\",\"partition\":\"batch\",\"state\":\"allocated\",\"cpus\":\"2\",\"memory\":\"3384\",\"tmpdisk\":\"82306\",\"weight\":\"16\"},{\"nodelist\":\"adev[10-15]\",\"nodes\":\"6\",\"partition\":\"batch\",\"state\":\"drain\",\"cpus\":\"2\",\"memory\":\"246\",\"tmpdisk\":\"82306\",\"weight\":\"16\"}]";
    public static final String partitions = "[{\"partition\":\"batch\",\"avail\":\"up\",\"timelimit\":\"infinite\",\"nodes\":\"2\\/6\\/0\\/8\",\"nodelist\":\"adev[8-15]\"},{\"partition\":\"debug\",\"avail\":\"up\",\"timelimit\":\"30:00\",\"nodes\":\"0\\/8\\/0\\/8 \",\"nodelist\":\"adev[0-7]\"},{\"partition\":\"part3\",\"avail\":\"down\",\"timelimit\":\"30:00\",\"nodes\":\"0\\/0\\/0\\/0 \",\"nodelist\":\"null\"},{\"partition\":\"part4\",\"avail\":\"down\",\"timelimit\":\"infinite\",\"nodes\":\"0\\/0\\/0\\/0\",\"nodelist\":\"null\"}]";
    public static final String jobs = "[{\"partition\":\"batch\",\"avail\":\"up\",\"timelimit\":\"infinite\",\"nodes\":\"2\\/6\\/0\\/8\",\"nodelist\":\"adev[8-15]\"},{\"partition\":\"debug\",\"avail\":\"up\",\"timelimit\":\"30:00\",\"nodes\":\"0\\/8\\/0\\/8 \",\"nodelist\":\"adev[0-7]\"},{\"partition\":\"part3\",\"avail\":\"down\",\"timelimit\":\"30:00\",\"nodes\":\"0\\/0\\/0\\/0 \",\"nodelist\":\"null\"},{\"partition\":\"part4\",\"avail\":\"down\",\"timelimit\":\"infinite\",\"nodes\":\"0\\/0\\/0\\/0\",\"nodelist\":\"null\"}]";
}
