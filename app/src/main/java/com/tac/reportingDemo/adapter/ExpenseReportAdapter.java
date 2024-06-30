package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.ExpenseDetailsActivity;
import com.tac.reportingDemo.pojo.ExpensePojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExpenseReportAdapter extends RecyclerView.Adapter<ExpenseReportAdapter.MyViewHolder> {
    private Activity context;
    private List<ExpensePojo> mList;

    public ExpenseReportAdapter(Activity context, List<ExpensePojo> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_report_item, parent,
                false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ExpensePojo pojo = mList.get(position);
        holder.placeTo.setText(pojo.getPlaceTo());
        holder.date.setText(pojo.getWorkDate());
        holder.total.setText(pojo.getTotalAmount());
        holder.placeFrom.setText(pojo.getPlaceFrom());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDetails(pojo);
            }
        });

    }

    private void reportDetails(ExpensePojo pojo) {

        Intent intent = new Intent(context, ExpenseDetailsActivity.class);
        intent.putExtra("expense", new Gson().toJson(pojo));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.placeTo)
        TextView placeTo;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.totalAmount)
        TextView total;
        @BindView(R.id.parent)
        LinearLayout parent;
        @BindView(R.id.placeFrom)
        TextView placeFrom;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
