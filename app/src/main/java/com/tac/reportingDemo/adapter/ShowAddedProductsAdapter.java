package com.tac.reportingDemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.pojo.ProductPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowAddedProductsAdapter extends RecyclerView.Adapter<ShowAddedProductsAdapter.MyViewHolder>  {


    private Context context;
    private List<ProductPojo> mList;

    public ShowAddedProductsAdapter(Context context, List<ProductPojo> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_list_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductPojo model = mList.get(position);
        holder.mName.setText(model.getName());
        holder.mAmount.setText(model.getAmount());
        holder.mQty.setText(model.getQty());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.amount)
        Button mAmount;
        @BindView(R.id.name)Button mName;
        @BindView(R.id.qty)Button mQty;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
