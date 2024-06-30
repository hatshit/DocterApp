package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.BillingActivity;
import com.tac.reportingDemo.activity.HomeActivity;
import com.tac.reportingDemo.activity.ReportDetailsActivity;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.activity.chemist.ChemistReportDetailsActivity;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.MyViewHolder> implements Filterable {

    private List<AreaPojo> mList;

    private List<AreaPojo> mFilterList;
    private Activity mContext;
    private MySharedPreferences sp;
    private int type = 0;


    public DoctorAdapter(Activity context, List<AreaPojo> list, int type) {
        this.mContext = context;
        this.mList = list;
        mFilterList = list;
        sp = MySharedPreferences.getInstance(context);
        this.type = type;


    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                int count = mFilterList.size();
                final List<AreaPojo> list = new ArrayList<>();

                AreaPojo filterableString;
                for (int i = 0; i < count; i++) {
                    filterableString = mFilterList.get(i);
                    if (mFilterList.get(i).getName().toLowerCase().contains(filterString) ||
                            mFilterList.get(i).getId().contains(filterString)) {
                        list.add(filterableString);
                    }


                    results.values = list;
                    results.count = list.size();


                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (List<AreaPojo>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doctor_list_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameTv.setText(mList.get(position).getName());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.addGifts("");
                sp.addProduct("");
                sp.addSamples("");
//
                Calendar fromCalender = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        fromCalender.set(Calendar.YEAR, year);
                        fromCalender.set(Calendar.MONTH, monthOfYear);
                        fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        fromCalender.set(Calendar.HOUR_OF_DAY, 0);
                        fromCalender.set(Calendar.MINUTE, 0);
                        fromCalender.set(Calendar.SECOND, 0);
//                        Utils.makeToast("Selected Date is: " + dayOfMonth + " - " + monthOfYear + " - " + year);
                        sp.setUserInfo("doctorId", mList.get(position).getId());

                        Log.d("TAC", "MUR DOC ADAP : "+ monthOfYear);
                        int month = monthOfYear + 1;

                        String date = dayOfMonth + "/" + (month ) + "/" + year;
                        if (String.valueOf(dayOfMonth).length() <= 1) {
                            date = "0" + dayOfMonth + "/" + (month ) + "/" + year;

                        }
                        if (String.valueOf(month).length() <= 1) {
                            date = "" + dayOfMonth + "/" + "0" + month + "/" + year;

                        }

                        if (String.valueOf(month).length() <= 1 && String.valueOf(dayOfMonth).length() <= 1) {
                            date = "0" + dayOfMonth + "/" + "0" + month + "/" + year;

                        }

                        Log.d("TAC", "MUR DOCTOR ADAP TYPE: " + type);

                        if (type == 0) {
                            Intent intent = new Intent(mContext, BillingActivity.class);

                            intent.putExtra("date", date);
                            sp.setUserInfo("doctor", mList.get(position).getName());
                            sp.setUserInfo("date", date);
//
//                        intent.putExtra("day",dayOfMonth+"");
//                        intent.putExtra("month",monthOfYear+"");
//                        intent.putExtra("year",year+"");
                            mContext.startActivity(intent);
                        }

                        if (type == 4 ) {
                            Intent intent = new Intent(mContext, HomeActivity.class);

                            intent.putExtra("date", date);
                            sp.setUserInfo("doctor", mList.get(position).getName());
                            sp.setUserInfo("date", date);
//
//                        intent.putExtra("day",dayOfMonth+"");
//                        intent.putExtra("month",monthOfYear+"");
//                        intent.putExtra("year",year+"");
                            mContext.startActivity(intent);
                        }


                        if (type == 1) {
                            Intent intent = new Intent(mContext, ChemistBillingActivity.class);

                            intent.putExtra("date", date);
                            sp.setUserInfo("doctor", mList.get(position).getName());
                            sp.setUserInfo("date", date);
//
//                        intent.putExtra("day",dayOfMonth+"");
//                        intent.putExtra("month",monthOfYear+"");
//                        intent.putExtra("year",year+"");
                            mContext.startActivity(intent);
                        }
                        if (type == 2) {
                            Intent intent = new Intent(mContext,
                                    ReportDetailsActivity.class);

                            intent.putExtra("date", date);
                            sp.setUserInfo("doctor", mList.get(position).getId());
                            sp.setUserInfo("date", date);
                            mContext.startActivity(intent);

                        }
                        if (type == 3) {
                            Intent intent = new Intent(mContext,
                                    ChemistReportDetailsActivity.class);

                            intent.putExtra("date", date);
                            sp.setUserInfo("doctor", mList.get(position).getId());
                            sp.setUserInfo("date", date);
                            mContext.startActivity(intent);

                        }


                    }


                };
                DatePickerDialog dialog = new DatePickerDialog(mContext, fromDate, fromCalender
                        .get(Calendar.YEAR), fromCalender.get(Calendar.MONTH),
                        fromCalender.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
//                dialog.getDatePicker().setMinDate(1546300800000L);
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView nameTv;
        @BindView(R.id.parent)
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
