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


public class ManageDeptView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeptCard adapter;
    private List<String> deptList;
    private DatabaseReference usersRef;
    private FirebaseDatabase database;
    private RecyclerView.LayoutManager mLayoutManager;
    private String batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deptview);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("UsersTree");
        Bundle extras = getIntent().getExtras();
        batch = extras.getString("batch");
        recyclerView = findViewById(R.id.dept_list_recycleview);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        deptList = new ArrayList<>();

        adapter = new DeptCard(this, deptList, batch);
        recyclerView.setAdapter(adapter);
        usersRef.child(batch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = snapshot.getKey();
                    deptList.add(str);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
