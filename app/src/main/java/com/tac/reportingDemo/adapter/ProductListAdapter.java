package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ProductPojoItem1;
import com.tac.reportingDemo.storage.MySharedPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {


    private Activity context;
    private List<ProductPojoItem1> mList;

    public ProductListAdapter(Activity context, List<ProductPojoItem1> mList) {
        this.context = context;
        this.mList = mList;

    }

    @NonNull
    @Override
    public ProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_view_list_item, parent, false);

        return new ProductListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.MyViewHolder holder, int position) {
        ProductPojoItem1 pojo = mList.get(position);
        holder.productName.setText(pojo.getProductname());
        holder.productAmount.setText(pojo.getProductamount());
        holder.totalAmount.setText(pojo.getTotamount());
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
        @BindView(R.id.productName)
        TextView productName;
        @BindView(R.id.productAmount)
        TextView productAmount;
        @BindView(R.id.totalAmount)
        TextView totalAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
