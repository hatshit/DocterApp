package com.tac.reportingDemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.MyDoctorPojo;
import com.tac.reportingDemo.pojo.MyViewPlanPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPlanAdapter extends RecyclerView.Adapter<ViewPlanAdapter.MyViewHolder> {


    private Activity context;
    private List<MyViewPlanPojo> mList;
    private RequestQueue mRequestQue;
    private MySharedPreferences sp;

    public ViewPlanAdapter(Activity context, List<MyViewPlanPojo> mList) {
        this.context = context;
        this.mList = mList;
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_view_plan_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyViewPlanPojo pojo = mList.get(position);

        try
        {
            holder.tvDate.setText(pojo.getPlandate());
            holder.tvMonthWeekDay.setText("Month - "+pojo.getMonth()+", Day - "+pojo.getDay()+", Week - "+pojo.getWeek());
            if(pojo.getType().toString().equals("1"))
                holder.tvDepartment.setText("Department : Doctor");
            else
                holder.tvDepartment.setText("Department : Chemist");
            holder.tvArea.setText("Area : "+pojo.getAreaname());
            holder.tvDoctor.setText("Docter Name : "+pojo.getName());
            holder.tvRemark.setText("Remark : "+pojo.getRemark());

            holder.imgDelete.setOnClickListener(v -> {
                showDeleteDialog(pojo, position);
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void showDeleteDialog(MyViewPlanPojo pojo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Plan?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.remove(position);
                notifyDataSetChanged();

                deleteData(pojo);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    private void deleteData(MyViewPlanPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DELETE_PLAN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Doctor Deleted!",
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
                    params.put("planId", pojo.getPlanId());
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMonthWeekDay)
        TextView tvMonthWeekDay;

        @BindView(R.id.imgDelete)
        ImageView imgDelete;

        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvDepartment)
        TextView tvDepartment;
        @BindView(R.id.tvArea)
        TextView tvArea;
        @BindView(R.id.tvDoctor)
        TextView tvDoctor;
        @BindView(R.id.tvRemark)
        TextView tvRemark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
