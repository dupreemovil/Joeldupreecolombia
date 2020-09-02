package com.dupreinca.dupree.mh_holders;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dupreeinca.lib_api_rest.model.dto.response.Faltante;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemFaltantesBinding;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class FaltantesHolder extends RecyclerView.ViewHolder{
    private ItemFaltantesBinding binding;
    private Events events;

    public FaltantesHolder(@NonNull ItemFaltantesBinding binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final Faltante item) {
        Resources resources = BaseAPP.getContext().getResources();
        binding.tvCode.setText(String.valueOf(item.getId()));
        binding.tvName.setText(item.getNombre());
        binding.tvPage.setText(resources.getString(R.string.concat_pagina, item.getPagina()));

      //  binding.cardViewBackGround.setCardBackgroundColor(getAdapterPosition()%2!=0 ? BaseAPP.getContext().getResources().getColor(R.color.transp_Accent) : BaseAPP.getContext().getResources().getColor(R.color.transp_azulDupree));

        binding.cardViewBackGround.setCardBackgroundColor(getAdapterPosition()%2!=0 ? BaseAPP.getContext().getResources().getColor(R.color.gray_4) : BaseAPP.getContext().getResources().getColor(R.color.gray_4));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null)
                    events.onClickRoot(item, getAdapterPosition());
            }
        });
    }

    public interface Events{
        void onClickRoot(Faltante dataRow, int row);
    }
}
