package com.example.hesz.labproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobsFragment extends Fragment implements DownloadCallback<String> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String TAG = "JobsFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL = "url_param";

    // TODO: Rename and change types of parameters
    private String url;

    private SwitchCompat myswitch;
    //private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;

    private ProgressBar pb;

    private static DataBaseHelper db;

    private boolean mDownloading = false;

    private List<JobItem> allJobsList = new ArrayList<>();
    private List<JobItem> runningJobsList = new ArrayList<>();
    private List<JobItem> completedJobsList= new ArrayList<>();
    private List<JobItem> otherJobsList = new ArrayList<>();


    // TODO: Rename and change types of parameters
    private String mParam1;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    private OnFragmentInteractionListener mListener;

    public JobsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment JobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobsFragment newInstance(String param1) {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        args.putString(URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);

        }
        db = new DataBaseHelper(this.getContext());



        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.
        mSectionsPagerAdapter = new JobsFragment.SectionsPagerAdapter(getChildFragmentManager());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_jobs, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.title_jobs);
        }

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);


        pb = view.findViewById(R.id.progressbar);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Set up the ViewPager with the sections adapter.
        mViewPager = view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        startDownload();


        return view;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"OncreateOptionsMenu");
        inflater.inflate(R.menu.menu_tabbed, menu);
        myswitch = menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switchForActionBar);
        myswitch.setChecked(((BottomNavigationActivity)getActivity()).startBackgroundTask);
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((BottomNavigationActivity)getActivity()).switchSwitched(isChecked);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void startDownload() {
        if (!mDownloading) {
            // Execute the async download.
            mDownloading = true;

            //updateFromDownload(Content.jobs);

            //TODO: uncomment for downloading the data
            mDownloadTask = new DownloadTask(this);
            mDownloadTask.execute(url);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        finishDownloading();
        mListener = null;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void updateFromDownload(String result) {
        try {
            Log.d(TAG,result);
            JSONArray jsonArray = new JSONArray(result);
            new AsyncTask<JSONArray,Void,Void>(){

                @Override
                protected Void doInBackground(JSONArray... values) {
                    try {
                        List<JobItem> jobList = JobItem.parseJobsFromJSON(values[0]);
                        clearAllLists();
                        for(JobItem job: jobList){
                            allJobsList.add(job);
                            switch (job.status){
                                case "running":
                                    runningJobsList.add(job);
                                    break;
                                case "completed":
                                    completedJobsList.add(job);
                                    break;
                                default:
                                    otherJobsList.add(job);
                                    break;
                            }



                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    pb.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    notifyFragmentsOfListChanges();
                    compareToDatabase();
                }


            }.execute(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void compareToDatabase() {
        new AsyncTask<Void,String,Void>(){
            LinkedList<JobItem> jobsFromDataBase;

            @Override
            protected Void doInBackground(Void... voids) {
                jobsFromDataBase = db.getALlJobs();
                for(JobItem downloadedJob: allJobsList){
                    boolean found = false;
                    for(JobItem dbJob: jobsFromDataBase){
                        if(downloadedJob.jobId==dbJob.jobId){
                            found = true;
                            if(!downloadedJob.status.equals(dbJob.status)) {
                                publishProgress("Job " + downloadedJob.jobId +
                                        " changed from "+dbJob.status+" to "+downloadedJob.status);
                            }
                            db.modifyJob(downloadedJob);

                        }
                    }
                    if(!found){
                        db.createJob(downloadedJob);
                        publishProgress("New Job " + downloadedJob.jobId +
                                " with "+downloadedJob.status+" status");
                    }

                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                makeToast(values[0]);
            }

            @Override
            protected void onPostExecute(Void result) {
                //makeToast("End of processing");
            }

        }.execute();

    }


    private void makeToast(String message){
        Toast.makeText(getActivity(), message,
                Toast.LENGTH_LONG).show();

    }

    private void clearAllLists() {
        allJobsList.clear();
        completedJobsList.clear();
        otherJobsList.clear();
        runningJobsList.clear();
    }

    private void notifyFragmentsOfListChanges(){
        for( Fragment fragment : getChildFragmentManager().getFragments()) {
            JobContentFragment job_fragment = ((JobContentFragment) fragment);
            job_fragment.notifyDatasetChanged();
        }
    }


    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

        switch(progressCode) {
            //TODO:Make this useful
            // You can add UI behavior for memoryBarProgress updates here.
            case Progress.ERROR:
                Log.d(TAG,"Error memoryBarProgress update");
                break;
            case Progress.CONNECT_SUCCESS:
                Log.d(TAG,"Succesful connection!");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                Log.d(TAG,"Input stream successfully read");
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.d(TAG,"Processing input stream...");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.d(TAG,"Input stream succesfully processed!");
                break;
        }
    }

    @Override
    public void finishDownloading() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a JobContentFragment (defined as a static inner class below).
            //JobContentFragment JCF = JobContentFragment.newInstance(position);

            switch (position) {
                case 0:
                    return allJobsFragment.newInstance(position,allJobsList);
                case 1:
                    return runningJobsFragment.newInstance(position,runningJobsList);
                case 2:
                    return completedJobsFragment.newInstance(position,completedJobsList);
                case 3:
                    return otherJobsFragment.newInstance(position,otherJobsList);
            }

            return null;
        }


        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Running";
                case 2:
                    return "Completed";
                case 3:
                    return "Others";
            }

            return null;
        }
    }


    public static class allJobsFragment extends JobContentFragment{
        public allJobsFragment() {

        }
        public static allJobsFragment newInstance(int sectionNumber, List itemsList) {
            allJobsFragment fragment = new allJobsFragment();
            fragment.setList(itemsList);
            return fragment;
        }
    }

    public static class runningJobsFragment extends JobContentFragment{
        public runningJobsFragment() {

        }
        public static runningJobsFragment newInstance(int sectionNumber, List itemsList) {
            runningJobsFragment fragment = new runningJobsFragment();
            fragment.setList(itemsList);
            return fragment;
        }
    }

    public static class completedJobsFragment extends JobContentFragment{
        public completedJobsFragment() {
    }
        public static completedJobsFragment newInstance(int sectionNumber, List itemsList) {
            completedJobsFragment fragment = new completedJobsFragment();
            fragment.setList(itemsList);
            return fragment;
        }
    }

    public static class otherJobsFragment extends JobContentFragment{
        public otherJobsFragment() {
    }
        public static otherJobsFragment newInstance(int sectionNumber, List itemsList) {
            otherJobsFragment fragment = new otherJobsFragment();
            fragment.setList(itemsList);
            return fragment;
        }
    }

}
