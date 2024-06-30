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
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGiftAdapter extends RecyclerView.Adapter<AddGiftAdapter.MyViewHolder> {

    private List<AreaPojo> mList;
    private Activity context;
    private MySharedPreferences sp;
    private int count = 0;

    public AddGiftAdapter(Activity context, List<AreaPojo> mList) {
        this.mList = mList;
        this.context = context;
        sp = MySharedPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_gift_layout,
                parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AreaPojo pojo = mList.get(position);
        holder.name.setText(pojo.getName());
//        sp.addGifts("");
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amountAsInt = 0, qtyAsInt = 0;


                String qty = holder.qty.getText().toString();
//                String amount = holder.amount.getText().toString();

                if (qty.isEmpty()) {
                    Utils.makeToast("Please enter a valid quantity");
                    return;
                }

                try {
//                    amountAsInt = Integer.parseInt(amount);
                    qtyAsInt = Integer.parseInt(qty);

                } catch (NumberFormatException e) {

                }

//                if (amountAsInt<= 0){
//                    Utils.makeToast("Please enter a valid amount");
//                    return;
//                }
                if (qtyAsInt <= 0) {
                    Utils.makeToast("Please enter a valid Quantity");
                    return;
                }

                try {
                    List<String> productIds = new ArrayList<>();
                    JSONObject obj = new JSONObject();
                    Gson gson = new Gson();
                    if (!sp.getGifts().equals("")) {
                        productIds = gson.fromJson(sp.getGiftIds(), ArrayList.class);
                        obj = new JSONObject(sp.getGifts());
                    }

                    JSONObject productObj = new JSONObject();
                    productObj.put("id", pojo.getId());
                    productObj.put("name", pojo.getName());
                    productObj.put("qty", qty);
//                    productObj.put("amount",amount );
                    obj.put(pojo.getId(), productObj);
                    if (!productIds.contains(pojo.getId())) {
                        productIds.add(pojo.getId());

                    }
                    Log.d("TAC", "MUR GIFT ADDED: " + obj.toString());
                    sp.addGiftIds(gson.toJson(productIds));
                    sp.addGifts(obj.toString());

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

//                    holder.parent.setVisibility(View.GONE);
                    count++;
                    changeViewToAdded(holder);

//                    mList.remove(position);
//                    notifyItemRemoved(position);
//                    holder.qty.setText("");

//                    notifyDataSetChanged();
//                    holder.amount.setText("");
//                    holder.qty.setText("");
//                    ((AddProductsActivity) context).removeView(position);
                } catch (JSONException e) {

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
        holder.addBtn.setText("Added");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.qty)
        EditText qty;

        @BindView(R.id.add)
        TextView addBtn;

        @BindView(R.id.parent)
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
