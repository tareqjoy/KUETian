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

import com.google.firebase.database.DataSnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeptCard extends RecyclerView.Adapter<DeptCard.MyViewHolder> {

    private Context mContext;
    private List<String> deptlist;
    private String batchRef;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deptText;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            deptText = (TextView) view.findViewById(R.id.deptName);
            cardView=view.findViewById(R.id.dept_card_view);
        }
    }


    public DeptCard(Context mContext, List<String> batchlist, String batchRef) {
        this.mContext = mContext;
        this.deptlist = batchlist;
        this.batchRef=batchRef;
    }

    @Override
    public DeptCard.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dept_card, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String singleDept = deptlist.get(position);

        holder.deptText.setText(singleDept);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ManageRollView.class);
                intent.putExtra("dept", singleDept);
                intent.putExtra("batch",batchRef);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return deptlist.size();
    }
}