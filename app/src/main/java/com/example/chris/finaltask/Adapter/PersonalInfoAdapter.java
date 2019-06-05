package com.example.chris.finaltask.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chris.finaltask.Class.PersonalInfo;
import com.example.chris.finaltask.R;

import java.util.List;

public class PersonalInfoAdapter extends RecyclerView.Adapter<PersonalInfoAdapter.ViewHolder> {

    private List<PersonalInfo> mInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Info;
        TextView Data;

        public ViewHolder(View view){
            super(view);
            Info = (TextView) view.findViewById(R.id.info);
            Data = (TextView) view.findViewById(R.id.data);
        }
    }

    public PersonalInfoAdapter(List<PersonalInfo> infoList){
        mInfoList = infoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_personal_info,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        PersonalInfo info = mInfoList.get(position);
        holder.Info.setText(info.getInfo());
        holder.Data.setText(info.getData());
    }

    @Override
    public int getItemCount(){
        return mInfoList.size();
    }
}
