package com.harry9425.notperish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class inventoryexpireadpater extends RecyclerView.Adapter<inventoryexpireadpater.viewholder> {

    ArrayList<productmodel> list;
    Context context;
    DatabaseReference databaseReference;

    public inventoryexpireadpater(ArrayList<productmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_expirefoodview, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        final productmodel detailsm = list.get(position);
        holder.name.setSelected(true);
        holder.date.setSelected(true);
        holder.name.setText(detailsm.getName());
        holder.date.setText(detailsm.getDate());
        Glide.with(context)
                .load(detailsm.getDp())
                .placeholder(R.drawable.logoloading)
                .into(holder.imageView);
        holder.date.setText(detailsm.getDate());
        holder.exdate.setText(detailsm.getExpiry());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredlist(ArrayList<productmodel> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        ImageView imageView,barcodeimage;
        TextView name,exdate,date;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.expiryfood_dp);
            name=itemView.findViewById(R.id.expirefood_name);
            date=itemView.findViewById(R.id.expiryfood_mmfgdate);
            exdate=itemView.findViewById(R.id.expirefood_expiry);
            barcodeimage=itemView.findViewById(R.id.expiryfood_barid);
        }
    }
}