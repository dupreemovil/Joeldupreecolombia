package com.dupreinca.dupree.mh_holders;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dupreeinca.lib_api_rest.model.dto.response.ItemPQR;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemSeguimientoPqrBinding;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class PQRHolder extends RecyclerView.ViewHolder{
    private ItemSeguimientoPqrBinding binding;
    private Events events;

    public PQRHolder(@NonNull ItemSeguimientoPqrBinding binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final ItemPQR item) {
        int position = getAdapterPosition();
        Resources resources = BaseAPP.getContext().getResources();
        
        binding.tvCaso.setText(String.valueOf(item.getCaso()));
        binding.tvfecha.setText(item.getFecha());
        binding.tvUsuario.setText(String.valueOf(item.getUsuario()));
        binding.tvGestion.setText(String.valueOf(item.getGestion()));
        binding.tvRazon.setText(item.getRazon());
        binding.tvDescripcion.setText(String.valueOf(item.getDescripcion()));

//        binding.cardViewBackGround.setCardBackgroundColor(position%2!=0 ? resources.getColor(R.color.transp_Accent) : resources.getColor(R.color.transp_azulDupree));


        binding.cardViewBackGround.setCardBackgroundColor(position%2!=0 ? resources.getColor(R.color.gray_4) : resources.getColor(R.color.gray_4));


        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null)
                    events.onClickRoot(item, getAdapterPosition());
            }
        });
    }

    public interface Events{
        void onClickRoot(ItemPQR dataRow, int row);
    }
}
