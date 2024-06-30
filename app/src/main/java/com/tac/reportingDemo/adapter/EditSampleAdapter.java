package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.BillingActivity;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.pojo.ProductPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditSampleAdapter extends RecyclerView.Adapter<EditSampleAdapter.MyViewHolder> {

    private List<ProductPojo> mList;
    private Activity context;
    private MySharedPreferences sp;
    private int count = 0;

    public EditSampleAdapter(Activity context, List<ProductPojo> mList) {
        this.mList = mList;
        this.context = context;
        sp = MySharedPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_gift_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductPojo pojo = mList.get(position);
        holder.name.setText(pojo.getName());
        holder.qty.setText(pojo.getQty());
        holder.addBtn.setText("Update");
//        sp.addProduct("");


        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAC", "MUR product data added :0 ");
                int qtyAsInt = 0;


                String qty = holder.qty.getText().toString();

                if (qty.isEmpty()) {
                    Utils.makeToast("Please enter a valid quantity");
                    return;
                }

                try {
                    qtyAsInt = Integer.parseInt(qty);

                } catch (NumberFormatException e) {

                }


                if (qtyAsInt <= 0) {
                    Utils.makeToast("Please enter a valid Quantity");
                    return;
                }

                try {
                    Log.d("TAC", "MUR product data added :5 ");

                    JSONObject obj = new JSONObject();
                    if (!sp.getSamples().equals("")) {
                        obj = new JSONObject(sp.getSamples());
                    }
                    Log.d("TAC", "MUR product data added :1 ");

                    JSONObject productObj = new JSONObject();
                    productObj.put("id", pojo.getId());
                    productObj.put("name", pojo.getName());
                    productObj.put("qty", qty);

                    obj.put(pojo.getId(), productObj);
                    Log.d("TAC", "MUR product data added : 2");


                    sp.addSamples(obj.toString());

                    if (count >= (mList.size() - 1)) {

                        if (sp.getBillingType().equals("chemist")) {
                            Intent intent = new Intent(context, ChemistBillingActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, BillingActivity.class);
                            context.startActivity(intent);
                        }
                        return;
                    }


//                    holder.mParent.setVisibility(View.GONE);
                    count++;
                    changeViewToAdded(holder);
//                    mList.remove(position);
//                    notifyDataSetChanged();
//                    ((AddProductsActivity) context).scrollToTop(0);

//                    mList.remove(position);
//                    holder.qty.setText("");
//                    holder.amount.setText("");


                    Log.d("TAC", "MUR product data added : 3");
//                    notifyItemRemoved(position);
//                    notifyDataSetChanged();
//                    holder.amount.setText("");
//                    holder.qty.setText("");
//                    ((AddProductsActivity) context).remo veView(position);
                    Log.d("TAC", "MUR product data added : 4");
                } catch (JSONException e) {


                }


            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("TAC", "MUR product data removed :5 ");
                    List<String> productIds = new ArrayList<>();
                    JSONObject obj = new JSONObject();
                    Gson gson = new Gson();
                    if (!sp.getSamples().equals("")) {
                        obj = new JSONObject(sp.getSamples());
                        productIds = gson.fromJson(sp.getSampleIds(), ArrayList.class);

                    }

                    obj.remove(pojo.getId());
                    productIds.remove(pojo.getId());
                    sp.addSampleIds(gson.toJson(productIds));
                    sp.addSamples(obj.toString());
                    if (productIds.size() < 1) {
                        sp.addProduct("");
                        sp.addProductId("");
                        if (sp.getBillingType().equals("chemist")) {
                            Intent intent = new Intent(context, ChemistBillingActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, BillingActivity.class);
                            context.startActivity(intent);
                        }                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void changeViewToAdded(MyViewHolder holder) {
        holder.qty.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.name.setFocusable(false);
        holder.qty.setFocusable(false);
        holder.addBtn.setEnabled(false);
        holder.addBtn.setText("Updated");
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        EditText name;
        @BindView(R.id.qty)
        EditText qty;
        //        @BindView(R.id.amount)
//        EditText amount;
        @BindView(R.id.add)
        TextView addBtn;
        @BindView(R.id.parent)
        LinearLayout mParent;
        //        @BindView(R.id.total)
//        EditText total;
        @BindView(R.id.remove)
        TextView remove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            remove.setVisibility(View.VISIBLE);
        }
    }
}
