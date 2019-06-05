package com.example.chris.finaltask.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chris.finaltask.Class.ConferenceInfo;
import com.example.chris.finaltask.R;

import java.util.List;

public class ConferenceInfoAdapter extends RecyclerView.Adapter<ConferenceInfoAdapter.ViewHolder> {

    private List<ConferenceInfo> mInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView date;
        TextView city;
        TextView sponsor;

        public ViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            city = (TextView) view.findViewById(R.id.city);
            sponsor = (TextView) view.findViewById(R.id.sponsor);
        }
    }

    public ConferenceInfoAdapter(List<ConferenceInfo> infoList){
        mInfoList = infoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conference_info,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        ConferenceInfo info = mInfoList.get(position);
        holder.name.setText(info.getName());
        holder.date.setText(info.getDate());
        holder.city.setText(info.getCity());
        holder.sponsor.setText(info.getSponsor());
    }

    @Override
    public int getItemCount(){
        return mInfoList.size();
    }
}
