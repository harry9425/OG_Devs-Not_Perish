package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class nearbyfood_layout extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<donatemodel> list=new ArrayList<>();
    FirebaseDatabase mDatabase;
    donatemodel donatemodel;
    FirebaseAuth mAuth;
    String uid;
    nearbyfoodadpter nearbyfoodadpter;
    String mycoor;
    int distancebw=1;
    TextView distancerem;
    ImageButton phone;
    EditText searchdonate;
    SeekBar seekBar;
    public  static int self=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbyfood_layout);
        distancerem=(TextView) findViewById(R.id.distanceshown2);
        searchdonate=(EditText) findViewById(R.id.searchalluser);
        seekBar=(SeekBar) findViewById(R.id.distance_slider2);
        recyclerView=(RecyclerView) findViewById(R.id.nearbyfood_bigrecycler);
        nearbyfoodadpter =new nearbyfoodadpter(list,this);
        recyclerView.setAdapter(nearbyfoodadpter);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        if(self==1){
            seekBar.setVisibility(View.GONE);
            getselflist();
        }
        else {
            seekBar.setMax(20);
            seekBar.setMin(1);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    distancebw=seekBar.getProgress();
                    distancerem.setText(seekBar.getProgress()+" Km");
                    getdonationlist();
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mycoor=snapshot.child("coordinates").getValue().toString();
                    getdonationlist();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            getdonationlist();
        }
        searchdonate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                filter(searchdonate.getText().toString().trim());
            }
        });
    }

    public void refres(View view){
        searchdonate.setText("");
        getdonationlist();
    }
    public void bkbk(View view){
        onBackPressed();
    }

    private void filter(String tosr) {

        ArrayList<donatemodel> temp=new ArrayList<donatemodel>();
        for(donatemodel friends:list)
        {
            if(friends.getName().contains(tosr))
            {
                temp.add(friends);
            }
        }
        nearbyfoodadpter.filteredlist(temp);
    }

    private void getdonationlist(){
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("donation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if(!snap.getKey().equals(uid)) {
                        for (DataSnapshot var : snap.getChildren()) {
                            String percoor = var.child("latltd").getValue().toString();
                            String[] s = percoor.split("&");
                            Double perlat = Double.parseDouble(s[0]);
                            Double perlong = Double.parseDouble(s[1]);
                            String[] p = mycoor.split("&");
                            Double mylat = Double.parseDouble(p[0]);
                            Double mylong = Double.parseDouble(p[1]);
                            float[] results = new float[1];
                            Location.distanceBetween(mylat, mylong,
                                    perlat, perlong, results);
                            float dist = results[0] / 1000;
                            donatemodel = new donatemodel();
                            donatemodel = var.getValue(donatemodel.class);
                            // Toast.makeText(mapdonate.this, dist+"\n"+distancebw, Toast.LENGTH_LONG).show();
                            if (dist <= distancebw * 1.0f) {
                                donatemodel = new donatemodel();
                                donatemodel = var.getValue(donatemodel.class);
                                // Toast.makeText(startpage.this,donatemodel.getUid(), Toast.LENGTH_LONG).show();
                                list.add(donatemodel);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(nearbyfood_layout.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getselflist(){
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("donation").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot s:snapshot.getChildren()){
                    donatemodel = new donatemodel();
                    donatemodel = s.getValue(donatemodel.class);
                    list.add(donatemodel);
                }
                nearbyfoodadpter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}