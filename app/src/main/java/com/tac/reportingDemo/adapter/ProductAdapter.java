package com.tac.reportingDemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    List<String> productsList;

    public ProductAdapter(List<String> productList) {
        productsList = productList;
    }

    public void filterList(ArrayList<String> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        productsList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(productsList.get(position));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtName)
        TextView txtName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
