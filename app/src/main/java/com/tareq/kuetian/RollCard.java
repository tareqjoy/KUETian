package com.tareq.kuetian;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RollCard extends RecyclerView.Adapter<RollCard.MyViewHolder> {

    private Context mContext;
    private List<String> uidlist;
    private List<String> rolllist;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rollnoText;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            rollnoText = (TextView) view.findViewById(R.id.rollNumber);
            cardView=view.findViewById(R.id.roll_card_view);
        }
    }


    public RollCard(Context mContext, List<String> rolllist, List<String> uidList) {
        this.mContext = mContext;
        this.rolllist = rolllist;
        this.uidlist = uidList;
    }

    @Override
    public RollCard.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.roll_card, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String singleRollNo = rolllist.get(position);
        final String singleUid = uidlist.get(position);

        holder.rollnoText.setText(singleRollNo);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("user","guest");
                intent.putExtra("uid", singleUid);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return rolllist.size();
    }
}