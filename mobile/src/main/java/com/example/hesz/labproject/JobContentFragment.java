package com.example.hesz.labproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hesz on 1/9/2018.
 */

public class JobContentFragment extends Fragment {
    private static final String TAG = "JobContentFragment";


    private RecyclerView recyclerView;

    private MyItemRecyclerViewAdapter adapter;

    private List mItems;




    public JobContentFragment() {
    }

    public void notifyDatasetChanged(){
        adapter.notifyDataSetChanged();
    }


    public void setList(List itemsList){
        mItems = itemsList;
        adapter = new MyItemRecyclerViewAdapter(mItems);

    }

    public static JobContentFragment newInstance(List itemsList) {
        JobContentFragment fragment = new JobContentFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder> {

        public final List<JobItem> mValues;

        public MyItemRecyclerViewAdapter(List<JobItem> items) {
            mValues = items;
        }

        @Override
        public com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
            return new com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).jobName);
            holder.mContentView.setText(mValues.get(position).status);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public JobItem mItem;

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

