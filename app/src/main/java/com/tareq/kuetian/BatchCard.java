package com.tareq.kuetian;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BatchCard extends RecyclerView.Adapter<BatchCard.MyViewHolder> {

    private Context mContext;

    private List<String> batchlist;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView batchnoText;
        public CardView cardView;
        String strData;

        public MyViewHolder(View view) {
            super(view);
            batchnoText = (TextView) view.findViewById(R.id.batchNumber);
            cardView=view.findViewById(R.id.batch_card_view);
        }
    }


    public BatchCard(Context mContext, List<String> batchlist) {
        this.mContext = mContext;
        this.batchlist = batchlist;

    }

    @Override
    public BatchCard.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_card, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String singleBatchNo = batchlist.get(position);

        holder.batchnoText.setText(singleBatchNo);
        holder.strData = singleBatchNo;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ManageDeptView.class);
                intent.putExtra("batch", holder.strData);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return batchlist.size();
    }
}