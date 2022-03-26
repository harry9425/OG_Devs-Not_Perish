package com.harry9425.notperish;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class nearbyfoodadpter extends RecyclerView.Adapter<nearbyfoodadpter.viewholder> {

    ArrayList<donatemodel> list;
    Context context;
    DatabaseReference databaseReference;

    public nearbyfoodadpter(ArrayList<donatemodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.samplenearbyfood, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        final donatemodel detailsm = list.get(position);
        holder.name.setSelected(true);
        holder.name.setText(detailsm.getName());
        holder.location.setText(detailsm.getLocation());
        holder.location.setSelected(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(detailsm.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(context)
                        .load(snapshot.child("dp").getValue().toString())
                        .placeholder(R.drawable.defaultuserdp)
                        .into(holder.imageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,donateview.class);
                donateview.id=detailsm.getUid()+"&"+detailsm.getDonationid()+"&"+detailsm.getLatltd();
                context.startActivity(i);
            }
        });
    }

    public void filteredlist(ArrayList<donatemodel> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        TextView name,location;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.accountsdp);
            name=itemView.findViewById(R.id.productname);
            location=itemView.findViewById(R.id.accountlocation);
        }
    }
}