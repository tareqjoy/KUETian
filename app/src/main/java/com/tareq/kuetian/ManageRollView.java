package com.tareq.kuetian;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ManageRollView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RollCard adapter;
    private List<String> rollList;
    private List<String> uidList;
    private DatabaseReference usersRef;
    private FirebaseDatabase database;
    private RecyclerView.LayoutManager mLayoutManager;
    private String batch, dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rollview);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("UsersTree");
        Bundle extras = getIntent().getExtras();
        batch = extras.getString("batch");
        dept = extras.getString("dept");
        recyclerView = findViewById(R.id.roll_list_recycleview);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        rollList = new ArrayList<>();
        uidList = new ArrayList<>();

        adapter = new RollCard(this, rollList, uidList);
        recyclerView.setAdapter(adapter);
        usersRef.child(batch).child(dept).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = snapshot.getKey();
                    rollList.add(str);
                    String value = dataSnapshot.child(str).getValue(String.class);
                    uidList.add(value);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
