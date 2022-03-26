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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class inventoryadapter extends RecyclerView.Adapter<inventoryadapter.viewholder> {

    ArrayList<productmodel> list;
    Context context;
    DatabaseReference databaseReference;

    public inventoryadapter(ArrayList<productmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sampleproductview, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        final productmodel detailsm = list.get(position);
        holder.name.setSelected(true);
        holder.date.setSelected(true);
        holder.name.setText(detailsm.getName());
        holder.date.setText(detailsm.getExpiry());
        Glide.with(context)
                .load(detailsm.getDp())
                .placeholder(R.drawable.logoloading)
                .into(holder.imageView);
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

        RoundedImageView imageView;
        TextView name,date;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.productdpview);
            name=itemView.findViewById(R.id.productnameview);
            date=itemView.findViewById(R.id.productdateview);
        }
    }
}