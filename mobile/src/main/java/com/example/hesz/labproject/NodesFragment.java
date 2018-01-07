package com.example.hesz.labproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NodesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NodesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NodesFragment extends Fragment implements DownloadCallback<String>{

    private static final String TAG = "NodesFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL = "url_param";

    // TODO: Rename and change types of parameters
    private String url;


    //private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;

    private boolean mDownloading = false;

    private TextView downloadTextView;
    private RecyclerView recyclerView;

    private MyItemRecyclerViewAdapter adapter;

    private OnFragmentInteractionListener mListener;


    public NodesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NodesFragment newInstance(String param1) {
        NodesFragment fragment = new NodesFragment();
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
        View view = inflater.inflate(R.layout.fragment_nodes, container, false);
        downloadTextView = view.findViewById(R.id.nodes_text_view);


        recyclerView = (RecyclerView) view.findViewById(R.id.list);


        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyItemRecyclerViewAdapter(new ArrayList<NodeItem>());
            recyclerView.setAdapter(adapter);

        }

        return view;
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
        startDownload();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        finishDownloading();
        mListener = null;
    }

    @Override
    public void updateFromDownload(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            downloadTextView.setText(result);
            adapter.swapValues(NodeItem.parseNodesFromJSON(jsonArray));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                Log.d(TAG,"Error progress update");
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

    private void startDownload() {
        if (!mDownloading) {
            // Execute the async download.
            mDownloading = true;
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
        void onFragmentInteraction(Uri uri);
    }



    private class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<com.example.hesz.labproject.NodesFragment.MyItemRecyclerViewAdapter.ViewHolder> {

        private final List<NodeItem> mValues;

        public MyItemRecyclerViewAdapter(List<NodeItem> items) {
            mValues = items;
        }

        public void swapValues (List<NodeItem> newValues){
            mValues.clear();
            mValues.addAll(newValues);
            notifyDataSetChanged();
        }


        @Override
        public com.example.hesz.labproject.NodesFragment.MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
            return new com.example.hesz.labproject.NodesFragment.MyItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final com.example.hesz.labproject.NodesFragment.MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).nodeName);
            holder.mContentView.setText(mValues.get(position).partitionName);


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

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public NodeItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }


    }

}


