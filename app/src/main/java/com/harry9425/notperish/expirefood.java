package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class expirefood extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<productmodel> list=new ArrayList<>();
    FirebaseDatabase mDatabase;
    productmodel productmodel;
    inventoryexpireadpater inventoryexpireadpater;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expirefood);
        recyclerView=(RecyclerView) findViewById(R.id.expire_recycler);
        inventoryexpireadpater =new inventoryexpireadpater(list,this);
        recyclerView.setAdapter(inventoryexpireadpater);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        getproductslist();
    }
    private void getproductslist(){
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("users").child(uid).child("inventory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    productmodel=new productmodel();
                    productmodel=snapshot.getValue(productmodel.class);
                    list.add(productmodel);
                }
                list.sort(new allfoodsorter());
                inventoryexpireadpater.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(expirefood.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void bkbk(View view){
        onBackPressed();
    }
    public void ref(View view){
        getproductslist();
    }
}