package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.MyViewHolder> {

    private List<AreaPojo> mList;
    private Activity context;
    private MySharedPreferences sp;
    private int count = 0;

    public AddProductAdapter(Activity context, List<AreaPojo> mList) {
        this.mList = mList;
        this.context = context;
        sp = MySharedPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_product_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AreaPojo pojo = mList.get(position);
        holder.name.setText(pojo.getName());
        holder.mrp.setText(pojo.getMrp());
        holder.amount.setText(pojo.getRate());
        holder.mrp.setEnabled(false);
        holder.mrp.setTextColor(context.getResources().getColor(R.color.black));
        holder.amount.setTextColor(context.getResources().getColor(R.color.black));
        holder.amount.setEnabled(false);
//        sp.addProduct("");

        holder.qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                double qtyInt, amntInt = 0;
                String amount = holder.amount.getText().toString().trim();
                String qty = holder.qty.getText().toString().trim();

                if (!amount.isEmpty() && !qty.isEmpty()) {
                    qtyInt = Integer.parseInt(qty);
                    amntInt = Double.parseDouble(amount);

                    double total = qtyInt * amntInt;
                    holder.mTotal.setText(total + "");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                double qtyInt, amntInt = 0;
                String amount = holder.amount.getText().toString().trim();
                String qty = holder.qty.getText().toString().trim();

                if (!amount.isEmpty() && !qty.isEmpty()) {
                    qtyInt = Integer.parseInt(qty);
                    amntInt = Double.parseDouble(amount);
                    double total = qtyInt * amntInt;
                    holder.mTotal.setText(total + "");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAC", "MUR product data added :0 ");
                double amountAsInt = 0, qtyAsInt = 0;


                String qty = holder.qty.getText().toString();
                String amount = holder.amount.getText().toString();
                String total = holder.mTotal.getText().toString();

                if (qty.isEmpty()) {
                    Utils.makeToast("Please enter a valid quantity");
                    return;
                }
                if (amount.isEmpty()) {
                    Utils.makeToast("Please enter a valid amount");
                    return;
                }
                try {
                    amountAsInt =Double.parseDouble(amount);
                    qtyAsInt = Integer.parseInt(qty);

                } catch (NumberFormatException e) {

                }

                if (amountAsInt <= 0) {
                    Utils.makeToast("Please enter a valid amount");
                    return;
                }
                if (qtyAsInt <= 0) {
                    Utils.makeToast("Please enter a valid Quantity");
                    return;
                }

                try {
                    Log.d("TAC", "MUR product data added :5 ");

                    JSONObject obj = new JSONObject();
                    Gson gson = new Gson();
                    List<String> productIds = new ArrayList<>();
                    if (!sp.getProduct().equals("")) {
                        obj = new JSONObject(sp.getProduct());
                        productIds = gson.fromJson(sp.getProductId(), ArrayList.class);
                    }
                    Log.d("TAC", "MUR product data added :1 ");

                    JSONObject productObj = new JSONObject();
                    productObj.put("id", pojo.getId());
                    productObj.put("name", pojo.getName());
                    productObj.put("qty", qty);
                    productObj.put("amount", amount);
                    productObj.put("total", total);
                    if (!productIds.contains(pojo.getId())) {
                        productIds.add(pojo.getId());

                    }
                    obj.put(pojo.getId(), productObj);
                    Log.d("TAC", "MUR product data added : 2");


                    sp.addProduct(obj.toString());

                    sp.addProductId(gson.toJson(productIds));

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
    }

    private void changeViewToAdded(MyViewHolder holder) {
        holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.mTotal.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.qty.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.name.setFocusable(false);
        holder.qty.setFocusable(false);
        holder.mTotal.setFocusable(false);
        holder.amount.setFocusable(false);
        holder.addBtn.setEnabled(false);
        holder.addBtn.setText("Added");
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
        @BindView(R.id.amount)
        EditText amount;

        @BindView(R.id.mrp)
        EditText mrp;
        @BindView(R.id.add)
        TextView addBtn;
        @BindView(R.id.parent)
        LinearLayout mParent;
        @BindView(R.id.total)
        EditText mTotal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
