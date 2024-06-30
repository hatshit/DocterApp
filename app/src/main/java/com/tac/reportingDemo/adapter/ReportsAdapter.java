package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.ReportDetailsActivity;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ReportPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    private List<ReportPojo> mList;
    private Activity context;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    public ReportsAdapter(Activity context, List<ReportPojo> mList) {
        this.mList = mList;
        this.context = context;
        sp = MySharedPreferences.getInstance(context);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rerport_list_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ReportPojo pojo = mList.get(position);
        holder.name.setText(pojo.getDoctorName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReportDetailsActivity.class);
                intent.putExtra("report", new Gson().toJson(pojo));
                context.startActivity(intent);
            }
        });
//        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showDeleteDialog(pojo,position);
//                return true;
//            }
//        });




    }
//
//
    private void deleteData(ReportPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Doctor Deleted.",
                            Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                    params.put("rptid", pojo.getId());
                    return params;
                }
            };
            mRequestQue.add(request);
        }

    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.parent)
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
