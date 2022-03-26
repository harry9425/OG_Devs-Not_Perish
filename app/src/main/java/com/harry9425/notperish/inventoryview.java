package com.harry9425.notperish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class inventoryview extends AppCompatActivity {

    ImageView imageView,brview;
    TextView name,id,exp,mfg;
    public static productmodel productmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventoryview);
        name=(TextView) findViewById(R.id.in_name);
        id=(TextView) findViewById(R.id.in_pid);
        exp=(TextView) findViewById(R.id.in_expiry);
        mfg=(TextView) findViewById(R.id.in_date);
        imageView=(ImageView) findViewById(R.id.in_image);
        name.setText(productmodel.getName());
        id.setText(productmodel.getPno());
        exp.setText(productmodel.getExpiry());
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date res = new Date(Long.parseLong(productmodel.getDate()));
        mfg.setText(obj.format(res));
        Glide.with(this)
                .load(productmodel.getDp())
                .placeholder(R.drawable.fooddefaultpic)
                .into(imageView);
    }
    public void bkbk(View view){
        onBackPressed();
    }
    public void delete(View view){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("You Really want to delete this item from your inventory");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.child("inventory").child(productmodel.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(inventoryview.this,"successfull",Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}