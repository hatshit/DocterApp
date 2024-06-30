package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.ProductListActivity;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.DoctorReportItem;
import com.tac.reportingDemo.pojo.ProductPojoItem1;
import com.tac.reportingDemo.storage.MySharedPreferences;

import java.io.Serializable;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {


    private Activity context;
    private List<DoctorReportItem> mList;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    public ReportAdapter(Activity context, List<DoctorReportItem> mList) {
        this.context = context;
        this.mList = mList;
        sp = MySharedPreferences.getInstance(context);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
    }

    @NonNull
    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);

        return new ReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.MyViewHolder holder, int position) {
        DoctorReportItem pojo = mList.get(position);
        holder.areaname.setText(pojo.getAreaname());
        holder.reportdate.setText(pojo.getReportdate());
        holder.name.setText(pojo.getDoctorname());
        holder.remark.setText(pojo.getRemark());
        holder.withwhom.setText(pojo.getWithwhom());

        holder.viewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ProductPojoItem1> products = pojo.getItemList();
                if (products != null && !products.isEmpty()) {
                    Intent i = new Intent(context, ProductListActivity.class);
                    i.putExtra("product", (Serializable) products);
                    context.startActivity(i);
                } else {
                    // Display a message indicating no product available
                    Toast.makeText(context, "No product available", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        holder.viewProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, ProductListActivity.class);
//                i.putExtra("product",(Serializable) pojo.getItemList());
//                context.startActivity(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.areaname)
        TextView areaname;
        @BindView(R.id.reportdate)
        TextView reportdate;

        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.withwhom)
        TextView withwhom;

        @BindView(R.id.viewProduct)
        Button viewProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
