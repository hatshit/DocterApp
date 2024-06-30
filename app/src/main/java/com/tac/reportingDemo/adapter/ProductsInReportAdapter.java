package com.tac.reportingDemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.pojo.ProductPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsInReportAdapter extends RecyclerView.Adapter<ProductsInReportAdapter.MyViewHolder> {

    private Context context;
    private List<ProductPojo> mList;
    private boolean isGift = false;

    public ProductsInReportAdapter(Context context, List<ProductPojo> mList, boolean isGift) {
        this.context = context;
        this.mList = mList;
        this.isGift = isGift;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_report_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ProductPojo pojo = mList.get(position);
        if (isGift) {
            holder.productIcon.setImageDrawable(context.getDrawable(R.drawable.ic_gift));
        }
        if (pojo.getAmount() != null) {
            holder.amount.setText(pojo.getAmount());
            holder.total.setText(pojo.getTotal());
        } else {

            holder.amountParent.setVisibility(View.GONE);
            holder.totalParent.setVisibility(View.GONE);
        }
        holder.name.setText(pojo.getName());
        holder.qty.setText(pojo.getQty());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.qty)
        TextView qty;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.total)
        TextView total;
        @BindView(R.id.amountParent)
        LinearLayout amountParent;

        @BindView(R.id.totalParent)
        LinearLayout totalParent;
        @BindView(R.id.productIcon)
        ImageView productIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
