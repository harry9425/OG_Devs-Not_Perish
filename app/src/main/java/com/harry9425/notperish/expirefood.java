package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
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

public class expirefood extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<productmodel> list=new ArrayList<>();
    FirebaseDatabase mDatabase;
    productmodel productmodel;
    inventoryexpireadpater inventoryexpireadpater;
    String uid;
    public static String val="";
    public static int fromlist=0,cnt=0;
    ImageButton searchbox,cancelbox,changserachbox,scanner;
    EditText editText;
    CardView searchview;
    Button continueval;
    String res="";
    int clicked=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expirefood);
        searchbox=(ImageButton) findViewById(R.id.search_mainexpiryview);
        changserachbox=(ImageButton) findViewById(R.id.search_expiryview);
        scanner=(ImageButton) findViewById(R.id.scanner_expiryview);
        cancelbox=(ImageButton) findViewById(R.id.close_expirylarge);
        editText=(EditText) findViewById(R.id.text_expiryview);
        searchview=(CardView) findViewById(R.id.cardview_expiryview);
        continueval=(Button) findViewById(R.id.savbtn_expiryview);
        recyclerView=(RecyclerView) findViewById(R.id.expire_recycler);
        inventoryexpireadpater =new inventoryexpireadpater(list,this);
        recyclerView.setAdapter(inventoryexpireadpater);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        getproductslist();
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
                    inventoryexpireadpater.filteredlist(list);
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
                        inventoryexpireadpater.filteredlist(temp);
                    } else {
                        ArrayList<productmodel> temp = new ArrayList<productmodel>();
                        for (productmodel friends : list) {
                            if (friends.getPno().contains(editText.getText().toString().trim())) {
                                temp.add(friends);
                            }
                        }
                        inventoryexpireadpater.filteredlist(temp);
                    }
                }
                else {
                    inventoryexpireadpater.filteredlist(list);
                }
            }
        });
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(expirefood.this);
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
                CodeScanner mCodeScanner = new CodeScanner(expirefood.this, scannerView);
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
        val="";
        getproductslist();
    }
    public void showrecipe(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view2 = getLayoutInflater().inflate(R.layout.alertshowrecipe, null);
        builder.setView(view2);
        AlertDialog alertDialog = builder.create();
        WebView webView = view2.findViewById(R.id.showrecipewebview);
        webView.getSettings().setJavaScriptEnabled(true);
        ArrayList<productmodel> recipelist=inventoryexpireadpater.setlist();
        ImageButton close=view2.findViewById(R.id.close_alertrecipe);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });
        if(recipelist.size()==0){
            Toast.makeText(this,"Choose atleat one item",Toast.LENGTH_LONG).show();
        }
        else {
            for (productmodel productmodel : recipelist) {
                val = val+productmodel.getName()+"+";
            }
            val=val.substring(0,val.length()-1);
            alertDialog.show();
            webView.loadUrl("https://www.allrecipes.com/search/results/?search=" + val);
        }
    }
    public void showdonate(View view){
        ArrayList<productmodel> donatelist=inventoryexpireadpater.setlist();
        if(donatelist.size()==0){
            Toast.makeText(this,"Choose atleat one item",Toast.LENGTH_LONG).show();
        }
        else {
            fromlist=1;
            donatepage.donatelist=donatelist;
            Intent i=new Intent(this,donatepage.class);
            startActivity(i);
        }
    }
}