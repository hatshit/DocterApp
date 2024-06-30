package com.tac.reportingDemo.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.tac.reportingDemo.R;
import com.tac.reportingDemo.pojo.NewsPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Activity context;
    private List<NewsPojo> mList;

    public NewsAdapter(Activity context, List<NewsPojo> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        NewsPojo pojo = mList.get(position);
        holder.heading.setText(pojo.getHeading());
        holder.date.setText(pojo.getDate());
        holder.news.setText(pojo.getNews());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailNews(pojo);
            }
        });

    }

    private void showDetailNews(NewsPojo pojo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("News Detail");
        builder.setMessage(pojo.getNews());
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news)
        TextView news;
        @BindView(R.id.heading)
        TextView heading;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.parent)
        RelativeLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
