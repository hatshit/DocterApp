package com.tac.reportingDemo.adapter;

import android.annotation.SuppressLint;
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
import com.tac.reportingDemo.activity.AddPlanAct;
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

public class DocterAdapterList extends RecyclerView.Adapter<DocterAdapterList.MyViewHolder> implements Filterable {

    private List<AreaPojo> mList;

    private List<AreaPojo> mFilterList;
    private Context mContext;
    private MySharedPreferences sp;


    public DocterAdapterList(Context context, List<AreaPojo> list) {
        this.mContext = context;
        this.mList = list;
        mFilterList = list;
        sp = MySharedPreferences.getInstance(context);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();
                final List<AreaPojo> list = new ArrayList<>();

                for (AreaPojo item : mFilterList) {
                    String name = item.getName().toLowerCase();
                    String id = item.getId().toLowerCase();

                    if (name.contains(filterString) || id.contains(filterString)) {
                        list.add(item);
                    }
                }

                results.values = list;
                results.count = list.size();

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.docter_list_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.areaTv.setText(mList.get(position).getName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddPlanAct) mContext).setDocter(position);
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
