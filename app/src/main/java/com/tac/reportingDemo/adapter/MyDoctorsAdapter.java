package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.MyDoctorPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDoctorsAdapter extends RecyclerView.Adapter<MyDoctorsAdapter.MyViewHolder> {


    private Activity context;
    private List<MyDoctorPojo> mList;
    private RequestQueue mRequestQue;
    private MySharedPreferences sp;

    public MyDoctorsAdapter(Activity context, List<MyDoctorPojo> mList) {
        this.context = context;
        this.mList = mList;
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_doctor_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyDoctorPojo pojo = mList.get(position);
        holder.hosptial.setText(pojo.getHospital());
        holder.mobile.setText(pojo.getMobile());
        holder.name.setText(pojo.getName());
        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(pojo, position);

                return true;
            }
        });

    }

    private void showDeleteDialog(MyDoctorPojo pojo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Doctor?");
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

    private void deleteData(MyDoctorPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DELETE_DOCTOR, new Response.Listener<String>() {
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
                    params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                    params.put("DId", pojo.getId());
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
        @BindView(R.id.parent)
        LinearLayout parent;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.hospital)
        TextView hosptial;
        @BindView(R.id.mobile)
        TextView mobile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
