package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.pojo.TourPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TourReportAdapter extends RecyclerView.Adapter<TourReportAdapter.MyViewHolder> {


    private Activity context;
    private List<TourPojo> mList;

    public TourReportAdapter(Activity context, List<TourPojo> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.area_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TourPojo pojo = mList.get(position);

        holder.area.setText(pojo.getPlace());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showReportDetails(pojo);
            }
        });


    }

    private void showReportDetails(TourPojo pojo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Detail");
        View view = LayoutInflater.from(context).inflate(R.layout.tour_report_detail, null, false);
        TextView place = view.findViewById(R.id.place);
        TextView toDate = view.findViewById(R.id.to_Date);
        TextView fromDate = view.findViewById(R.id.from_Date);
        TextView totalDays = view.findViewById(R.id.totalDays);
        TextView remark = view.findViewById(R.id.remark);

        place.setText(pojo.getPlace());
        toDate.setText(pojo.getToDate());
        fromDate.setText(pojo.getFromDate());
        totalDays.setText(pojo.getTotalDays());
        remark.setText(pojo.getRemark());

        builder.setView(view);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.area)
        TextView area;
        @BindView(R.id.parent)
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
