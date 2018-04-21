package com.tareq.kuetian;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ManageBatchView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BatchCard adapter;
    private List<String> batchList;
    private DatabaseReference usersRef;
    private FirebaseDatabase database;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.batchview);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("UsersTree");

        recyclerView = findViewById(R.id.batch_list_recycleview);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        batchList = new ArrayList<>();

        adapter = new BatchCard(this, batchList);
        recyclerView.setAdapter(adapter);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                batchList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = snapshot.getKey().toLowerCase();
                    batchList.add(str);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
