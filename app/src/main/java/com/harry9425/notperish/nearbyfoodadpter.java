package com.harry9425.notperish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
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
        holder.name.setText(detailsm.getDetails());
        holder.location.setText(detailsm.getLocation());
        holder.location.setSelected(true);
        Glide.with(context)
                .load(detailsm.getDp())
                .placeholder(R.drawable.logoloading)
                .into(holder.imageView);
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