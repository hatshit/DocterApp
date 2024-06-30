package com.tac.reportingDemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.AddDoctorActivity;
import com.tac.reportingDemo.activity.SelectDoctorActivity;
import com.tac.reportingDemo.activity.SelectDoctorForReportActivity;
import com.tac.reportingDemo.activity.chemist.AddChemistActivity;
import com.tac.reportingDemo.activity.chemist.SelectChemistActivity;
import com.tac.reportingDemo.activity.chemist.SelectChemistForReportActivity;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.MyViewHolder> implements Filterable {

    private List<AreaPojo> mList;

    private List<AreaPojo> mFilterList;
    private Context mContext;
    private MySharedPreferences sp;
    private int type = 0;


    public AreaAdapter(Context context, List<AreaPojo> list, int type) {
        this.mContext = context;
        this.mList = list;
        mFilterList = list;
        sp = MySharedPreferences.getInstance(context);
        this.type = type;

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                int count = mFilterList.size();
                final List<AreaPojo> list = new ArrayList<>();

                AreaPojo filterableString;
                for (int i = 0; i < count; i++) {
                    filterableString = mFilterList.get(i);
                    if (mFilterList.get(i).getName().toLowerCase().contains(filterString) ||
                            mFilterList.get(i).getId().contains(filterString)) {
                        list.add(filterableString);
                    }


                    results.values = list;
                    results.count = list.size();


                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (List<AreaPojo>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.area_list_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.areaTv.setText(mList.get(position).getName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 0) {
                    Intent intent = new Intent(mContext, SelectDoctorActivity.class);
                    intent.putExtra("area", mList.get(position).getName());
                    sp.setUserInfo("areaName", mList.get(position).getName());
                    sp.setUserInfo("areaId", mList.get(position).getId());
                    mContext.startActivity(intent);
                }
                if (type == 1) {
                    Intent intent = new Intent(mContext, SelectChemistActivity.class);
                    intent.putExtra("area", mList.get(position).getName());
                    sp.setUserInfo("areaName", mList.get(position).getName());
                    sp.setUserInfo("areaId", mList.get(position).getId());
                    mContext.startActivity(intent);
                }
                if (type == 2) {
                    Intent intent = new Intent(mContext, SelectDoctorForReportActivity.class);
                    intent.putExtra("area", mList.get(position).getName());
                    sp.setUserInfo("areaName", mList.get(position).getName());
                    sp.setUserInfo("areaId", mList.get(position).getId());
                    mContext.startActivity(intent);
                }
                if (type == 3) {
                    Intent intent = new Intent(mContext, SelectChemistForReportActivity.class);
                    intent.putExtra("area", mList.get(position).getName());
                    sp.setUserInfo("areaName", mList.get(position).getName());
                    sp.setUserInfo("areaId", mList.get(position).getId());
                    mContext.startActivity(intent);
                }
                if (type == 4) {
                    ((AddDoctorActivity) mContext).setArea(mList.get(position).getName());
                }
                if (type == 5) {
                    ((AddChemistActivity) mContext).setArea(mList.get(position).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.area)
        TextView areaTv;
        @BindView(R.id.parent)
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
