package com.example.hesz.labproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;

/**
 * Created by gergelyhesz on 09/01/2018.
 */

public class DataBaseHelper  extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "jobsDB";

    // Table Names
    private static final String TABLE_JOBS = "jobs";



    /*

      "jobid":"3456",
      "partition":"debug",
      "name":"Job 1 name",
      "user":"user 1",
      "status":"running",
      "time":"1-20:51:21",
      "nodes":"1",
      "nodelist":"adev0"

    */

    //Hunting Session table names
    private static final String KEY_ID = "id";
    private static final String KEY_PARTITION = "partition";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER = "user";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIME = "time";
    private static final String KEY_NODES = "nodes";
    private static final String KEY_NODELIST = "nodelist";


    private static final String CREATE_TABLE_JOBS = "CREATE TABLE " + TABLE_JOBS
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PARTITION + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_USER + " TEXT, "
            + KEY_STATUS + " TEXT, "
            + KEY_TIME + " TEXT, "
            + KEY_NODES + " INTEGER, "
            + KEY_NODELIST + " TEXT "
            + ")";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_JOBS);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);


        // create new tables
        onCreate(db);

    }



    //CRUD section

    public long createJob(JobItem job){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, job.jobId);
        values.put(KEY_PARTITION, job.partitionName);
        values.put(KEY_NAME, job.jobName);
        values.put(KEY_USER, job.submittingUser);
        values.put(KEY_STATUS, job.status);
        values.put(KEY_TIME, job.time);
        values.put(KEY_NODES, job.numberOfNodes);
        values.put(KEY_NODELIST, job.nodelist);

        // insert row
        long jobId = db.insert(TABLE_JOBS, null, values);


        return jobId;
    }



    public LinkedList<JobItem> getALlJobs() {
        SQLiteDatabase db = this.getWritableDatabase();
        LinkedList<JobItem> jobs = new LinkedList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_JOBS;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                JobItem job = new JobItem(
                        c.getInt(c.getColumnIndex(KEY_ID)),
                        c.getString(c.getColumnIndex(KEY_PARTITION)),
                        c.getString(c.getColumnIndex(KEY_NAME)),
                        c.getString(c.getColumnIndex(KEY_USER)),
                        c.getString(c.getColumnIndex(KEY_STATUS)),
                        c.getString(c.getColumnIndex(KEY_TIME)),
                        c.getInt(c.getColumnIndex(KEY_NODES)),
                        c.getString(c.getColumnIndex(KEY_NODELIST))
                );

                jobs.add(job);
            } while (c.moveToNext());
        }
        return jobs;
    }


    public int deleteJob(int jobId){
        SQLiteDatabase db= this.getWritableDatabase();

        return db.delete(TABLE_JOBS, KEY_ID + " = ?",
                new String []{String.valueOf(jobId)});
    }


    public int modifyJob(JobItem job){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PARTITION, job.partitionName);
        values.put(KEY_NAME, job.jobName);
        values.put(KEY_USER, job.submittingUser);
        values.put(KEY_STATUS, job.status);
        values.put(KEY_TIME, job.time);
        values.put(KEY_NODES, job.numberOfNodes);
        values.put(KEY_NODELIST, job.nodelist);

        return db.update(TABLE_JOBS,values, KEY_ID + " = ?",
                new String[]{String.valueOf(job.jobId)});
    }
}
