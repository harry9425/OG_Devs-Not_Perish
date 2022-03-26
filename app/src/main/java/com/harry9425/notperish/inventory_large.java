package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;

public class inventory_large extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<productmodel> list=new ArrayList<>();
    FirebaseDatabase mDatabase;
    productmodel productmodel;
    FirebaseAuth mAuth;
    String uid;
    ImageButton searchbox,cancelbox,changserachbox,scanner;
    EditText editText;
    String res;
    Button continueval;
    CardView searchview;
    int clicked=0;
    inventoryadapter inventoryadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_large);
        searchbox=(ImageButton) findViewById(R.id.search_inventory);
        changserachbox=(ImageButton) findViewById(R.id.search_inventorylarge);
        scanner=(ImageButton) findViewById(R.id.scanner_inventorylarge);
        cancelbox=(ImageButton) findViewById(R.id.close_inventorylarge);
        editText=(EditText) findViewById(R.id.text_inventorylarge);
        searchview=(CardView) findViewById(R.id.cardview_inventory);
        continueval=(Button) findViewById(R.id.savbtn_inventorylarge);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_inventory);
        inventoryadapter =new inventoryadapter(list,this);
        recyclerView.setAdapter(inventoryadapter);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        ImageButton bkbk=(ImageButton) findViewById(R.id.bkbk_inl);
        bkbk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bkbk(view);
            }
        });
        searchbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=0;
                if(searchbox.getAlpha()==1f){
                    searchbox.setAlpha(0.98f);
                    searchbox.setImageResource(R.drawable.ic_baseline_close_24);
                    searchview.setVisibility(View.VISIBLE);
                }
                else {
                    searchbox.setAlpha(1f);
                    searchbox.setImageResource(R.drawable.ic_baseline_search_24);
                    searchview.setVisibility(View.GONE);
                }
            }
        });
        cancelbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchview.setVisibility(View.GONE);
                searchbox.setAlpha(1f);
                if(clicked==0) {
                    inventoryadapter.filteredlist(list);
                }
                else {
                    clicked=0;
                }
                searchbox.setImageResource(R.drawable.ic_baseline_search_24);
            }
        });
        changserachbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changserachbox.getAlpha()==1f){
                    changserachbox.setAlpha(0.98f);
                    changserachbox.setImageResource(R.drawable.ic_baseline_numbers_24);
                    editText.setHint("Enter barcode code");
                }
                else {
                    changserachbox.setAlpha(1f);
                    editText.setHint("Enter product name");
                    changserachbox.setImageResource(R.drawable.ic_baseline_search_24);
                }
            }
        });
        continueval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=1;
                // Toast.makeText(inventory_large.this,"ddffd",Toast.LENGTH_LONG).show();
                if(!editText.getText().toString().isEmpty()) {
                    if (changserachbox.getAlpha()==1f) {
                        ArrayList<productmodel> temp = new ArrayList<productmodel>();
                        for (productmodel friends : list) {
                            if (friends.getName().contains(editText.getText().toString().trim())) {
                                temp.add(friends);
                            }
                        }
                        //Toast.makeText(inventory_large.this,list.get(0).getName().toString(),Toast.LENGTH_LONG).show();
                        inventoryadapter.filteredlist(temp);
                    } else {
                        ArrayList<productmodel> temp = new ArrayList<productmodel>();
                        for (productmodel friends : list) {
                            if (friends.getPno().contains(editText.getText().toString().trim())) {
                                temp.add(friends);
                            }
                        }
                        inventoryadapter.filteredlist(temp);
                    }
                }
                else {
                    inventoryadapter.filteredlist(list);
                }
            }
        });
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(inventory_large.this);
                final View view2=getLayoutInflater().inflate(R.layout.sampleqrscan,null);
                builder.setView(view2);
                AlertDialog alertDialog =builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                ImageButton reload=view2.findViewById(R.id.reload_alertqr);
                ImageButton done=view2.findViewById(R.id.done_alertqr);
                done.setEnabled(false);
                done.setAlpha(0.5f);
                CodeScannerView scannerView = view2.findViewById(R.id.scanner_view_alert);
                CodeScanner mCodeScanner = new CodeScanner(inventory_large.this, scannerView);
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull final Result result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                res=(result.getText());
                                done.setEnabled(true);
                                done.setAlpha(1f);
                            }
                        });
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changserachbox.setAlpha(0.98f);
                        changserachbox.setImageResource(R.drawable.ic_baseline_numbers_24);
                        editText.setHint("Enter barcode code");
                        editText.setText(res);
                        alertDialog.dismiss();
                        alertDialog.cancel();
                    }
                });
                scannerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCodeScanner.startPreview();
                    }
                });
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCodeScanner.startPreview();
                    }
                });
            }
        });
        getproductslist();
    }

    public void bkbk(View view){
        onBackPressed();
    }
    public void ref(View view){
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
                inventoryadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(inventory_large.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
}