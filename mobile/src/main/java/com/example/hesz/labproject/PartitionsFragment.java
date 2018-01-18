package com.example.hesz.labproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class PartitionsFragment extends Fragment implements DownloadCallback<String> {

    private static final String TAG = "PartitionsFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL = "url_param";

    private String url;


    //private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;

    private boolean mDownloading = false;

    private RecyclerView recyclerView;

    private ProgressBar pb;

    private PartitionsFragment.MyItemRecyclerViewAdapter adapter;

    private PartitionsFragment.OnFragmentInteractionListener mListener;

    private SwitchCompat myswitch;


    public PartitionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PartitionsFragment.
     */
    public static PartitionsFragment newInstance(String param1) {
        PartitionsFragment fragment = new PartitionsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_partitions, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.title_partitions);
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        pb = view.findViewById(R.id.progressbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);




        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            adapter = new PartitionsFragment.MyItemRecyclerViewAdapter(new ArrayList<PartitionItem>());
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);

        }

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
            mListener.OnFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PartitionsFragment.OnFragmentInteractionListener) {
            mListener = (PartitionsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
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
        Log.d(TAG, "update from dowmload, download finished: "+result);

        new AsyncTask<String,Void,List<PartitionItem>>(){

            @Override
            protected List doInBackground(String... result) {
                try {
                    JSONArray jsonArray = new JSONArray(result[0]);
                    return PartitionItem.parsePartitionsFromJSON(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<PartitionItem> newValues){
                pb.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.swapValues(newValues);
            }
        }.execute(result);
    }



    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

        switch(progressCode) {
            //TODO:Make this useful
            // You can add UI behavior for memoryBarProgress updates here.
            case DownloadCallback.Progress.ERROR:
                Log.d(TAG,"Error memoryBarProgress update");
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                Log.d(TAG,"Succesful connection!");
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                Log.d(TAG,"Input stream successfully read");
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.d(TAG,"Processing input stream...");
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
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

    private void startDownload() {
        Log.d(TAG,"starting download");
        if (!mDownloading) {
            // Execute the async download.
            mDownloading = true;

            //updateFromDownload(Content.partitions);

            //TODO: uncomment for downloading the data
            mDownloadTask = new DownloadTask(this);
            mDownloadTask.execute(url);
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
        void OnFragmentInteractionListener(Uri uri);
    }



    private class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<com.example.hesz.labproject.PartitionsFragment.MyItemRecyclerViewAdapter.ViewHolder> {

        private final List<PartitionItem> mValues;

        public MyItemRecyclerViewAdapter(List<PartitionItem> items) {
            Log.d(TAG,"MyItemRecyclerViewAdapter created");
            mValues = items;
        }

        public void swapValues (List<PartitionItem> newValues){
            mValues.clear();
            mValues.addAll(newValues);
            notifyDataSetChanged();
        }


        @Override
        public com.example.hesz.labproject.PartitionsFragment.MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_partition, parent, false);
            return new com.example.hesz.labproject.PartitionsFragment.MyItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final com.example.hesz.labproject.PartitionsFragment.MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTVpartition.setText(mValues.get(position).partitionName);
            holder.mTVavail.setText(mValues.get(position).availability);
            holder.mTVtimelimit.setText(mValues.get(position).timelimit);
            holder.mTVnodes.setText(mValues.get(position).nodeStates);
            holder.mTVnodelist.setText(mValues.get(position).nodelist);


            /*
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
            */
        }

        /*
 {
      "partition":"batch",
      "avail":"up",
      "timelimit":"infinite",
      "nodes":"2\/6\/0\/8",
      "nodelist":"adev[8-15]"
   },
    */

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTVpartition;
            public final TextView mTVavail;
            public final TextView mTVtimelimit;
            public final TextView mTVnodes;
            public final TextView mTVnodelist;
            public PartitionItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTVpartition = (TextView) view.findViewById(R.id.partition);
                mTVavail = (TextView) view.findViewById(R.id.avail);
                mTVtimelimit = (TextView) view.findViewById(R.id.timelimit);
                mTVnodes = (TextView) view.findViewById(R.id.nodes);
                mTVnodelist = (TextView) view.findViewById(R.id.nodelist);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTVpartition.getText() + "'";
            }
        }


    }


}
