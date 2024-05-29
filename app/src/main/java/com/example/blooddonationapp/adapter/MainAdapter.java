package com.example.blooddonationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.ui.DetailActivity;
import com.example.blooddonationapp.ui.ReadWriteUserDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private Context context;
    private List<ReadWriteUserDetails> datalist;
    private FirebaseUser firebaseUser;
    private FirebaseAuth authProfile;

    public MainAdapter(Context context, List<ReadWriteUserDetails> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ReadWriteUserDetails userDetails = datalist.get(position);
        if (!userDetails.isDataLocked())
        {
            authProfile = FirebaseAuth.getInstance();
            firebaseUser = authProfile.getCurrentUser();
            String imageUrl = userDetails.getImage();

            // Check if the image URL is blank or null
            if (imageUrl != null) {
                // Load the image using Glide
                Glide.with(context)
                        .load(imageUrl)
                        .into(holder.recImage);
            } else {
                holder.recImage.setImageResource(R.drawable.blank23);
            }
            //Glide.with(context).load(userDetails.getImage()).into(holder.recImage);
            holder.recName.setText(datalist.get(position).getName());
            holder.recBlood.setText(datalist.get(position).getBlood_Group());
            holder.recAddress.setText(datalist.get(position).getAddress());
            holder.recPhone.setText(datalist.get(position).getPhone());

            holder.recCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("Image",datalist.get(holder.getAbsoluteAdapterPosition()).getImage());
                    intent.putExtra("Name",datalist.get(holder.getAbsoluteAdapterPosition()).getName());
                    intent.putExtra("Blood_Group",datalist.get(holder.getAbsoluteAdapterPosition()).getBlood_Group());
                    intent.putExtra("Phone",datalist.get(holder.getAbsoluteAdapterPosition()).getPhone());
                    intent.putExtra("Address",datalist.get(holder.getAbsoluteAdapterPosition()).getAddress());

                    context.startActivity(intent);
                }
            });
        }
        else {
            holder.recCard.setVisibility(View.GONE);
        }
    }


    public void searchDataList(ArrayList<ReadWriteUserDetails>searchList)
    {
        datalist = searchList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    CircleImageView recImage;
    TextView recName,recBlood,recAddress,recPhone;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.img1);
        recName = itemView.findViewById(R.id.nametext);
        recBlood = itemView.findViewById(R.id.bloodtext);
        recAddress = itemView.findViewById(R.id.addresstext);
        recCard = itemView.findViewById(R.id.cardView);
        recPhone = itemView.findViewById(R.id.phonetext);
    }
}