package com.example.hesz.labproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
                    .inflate(R.layout.listitem_job, parent, false);
            return new com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTVjobid.setText(""+mValues.get(position).jobId);
            holder.mTVpartition.setText(mValues.get(position).partitionName);
            holder.mTVname.setText(mValues.get(position).jobName);
            holder.mTVuser.setText(mValues.get(position).submittingUser);
            holder.mTVstatus.setText(mValues.get(position).status);
            holder.mTVtime.setText(mValues.get(position).time);
            holder.mTVnodelist.setText(mValues.get(position).nodelist);
            if(holder.mItem.status.equals("running"))
                holder.mTVstatus.setTextColor(getResources().getColor(R.color.secondaryLightColor));
            else if(holder.mItem.status.equals("completed"))
                holder.mTVstatus.setTextColor(getResources().getColor(R.color.primaryLightColor));
            else
                holder.mTVstatus.setTextColor(Color.GRAY);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTVjobid;
            public final TextView mTVpartition;
            public final TextView mTVname;
            public final TextView mTVuser;
            public final TextView mTVstatus;
            public final TextView mTVtime;
            public final TextView mTVnodelist;
            public JobItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTVjobid = (TextView) view.findViewById(R.id.jobid);
                mTVpartition = (TextView) view.findViewById(R.id.partition);
                mTVname = (TextView) view.findViewById(R.id.name);
                mTVuser = (TextView) view.findViewById(R.id.user);
                mTVstatus = (TextView) view.findViewById(R.id.status);
                mTVtime = (TextView) view.findViewById(R.id.time);
                mTVnodelist = (TextView) view.findViewById(R.id.nodelist);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTVname.getText() + "'";
            }
        }
    }

}

