package com.example.hesz.labproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BottomNavigationActivity extends AppCompatActivity implements NodesFragment.OnFragmentInteractionListener, JobsFragment.OnFragmentInteractionListener, PartitionsFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;

    public boolean startBackgroundTask = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_jobs:
                    selectedFragment =  JobsFragment.newInstance(Constants.REST_SERVER_ADDRESS+Constants.JOBS_ADDRESS_SUFFIX);
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_frame_container, selectedFragment).commit();
                    break;
                case R.id.navigation_nodes:
                    selectedFragment = NodesFragment.newInstance(Constants.REST_SERVER_ADDRESS+Constants.NODES_ADDRESS_SUFFIX);
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_frame_container, selectedFragment).commit();
                    break;
                case R.id.navigation_queues:
                    selectedFragment = PartitionsFragment.newInstance(Constants.REST_SERVER_ADDRESS+Constants.PARTITIONS_ADDRESS_SUFFIX);
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_frame_container, selectedFragment).commit();
                    break;
            }
            return true;


         /*   switch (item.getItemId()) {
                case R.id.navigation_jobs:
                    mTextMessage.setText(R.string.title_jobs);
                    return true;
                case R.id.navigation_nodes:
                    mTextMessage.setText(R.string.title_nodes);
                    return true;
                case R.id.navigation_queues:
                    mTextMessage.setText(R.string.title_queues);
                    return true;
            }
            return false;*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent newTaskActivity = new Intent(BottomNavigationActivity.this, NewJobActivity.class);
                //Intent registerIntent = new Intent(LoginActivity.this, testActivity.class);
                startActivity(newTaskActivity);

                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        Intent intent = new Intent(this,DownloadBgService.class);
        stopService(intent);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment newFragment =  JobsFragment.newInstance(Constants.REST_SERVER_ADDRESS+Constants.JOBS_ADDRESS_SUFFIX);
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_frame_container, newFragment).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void OnFragmentInteractionListener(Uri uri) {

    }

    public void switchSwitched(boolean state){

        startBackgroundTask = state;
        if(state){

            Intent intent = new Intent(this,DownloadBgService.class);
            startService(intent);
        }
        else{
            Intent intent = new Intent(this,DownloadBgService.class);
            stopService(intent);
        }

    }
}
