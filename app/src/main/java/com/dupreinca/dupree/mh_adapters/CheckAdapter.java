package com.dupreinca.dupree.mh_adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.model_view.Opciones;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.MyViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(Opciones item);
        void onItemUncheck(Opciones item);
    }


    @NonNull
    public OnItemCheckListener onItemCheckListener;

    Context mContext;
    ArrayList<Opciones> opciones;


    public CheckAdapter(Context mContext, ArrayList<Opciones> opciones,OnItemCheckListener itemclick) {
        this.mContext = mContext;
        this.opciones = opciones;
        this.onItemCheckListener = itemclick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkbox, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtpregunta.setText(opciones.get(position).getOpcion());



        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkbox.isChecked()){

                    onItemCheckListener.onItemCheck(opciones.get(position));
                }
                else{
                    onItemCheckListener.onItemUncheck(opciones.get(position));

                }



            }
        });

    }

    @Override
    public int getItemCount() {

        return opciones.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtpregunta;
        public CheckBox checkbox;


        public MyViewHolder(View view) {
            super(view);

            txtpregunta = view.findViewById(R.id.txtcheck);
            checkbox = view.findViewById(R.id.checkBox);

        }
    }
}